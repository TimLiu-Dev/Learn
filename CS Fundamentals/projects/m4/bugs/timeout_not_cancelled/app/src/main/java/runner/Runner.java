package runner;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runs a plan (jobs already in topological order): a producer thread fills a bounded
 * {@link ArrayBlockingQueue}, a dispatcher takes a {@link Semaphore} slot and then a job,
 * and each job runs on its own virtual thread.
 */
public final class Runner {
    private static final Job END = new Job("<end>", 0, 0, List.of());   // poison pill: no more jobs

    private enum Attempt { OK, FAILED, TIMED_OUT }

    private final List<Job> plan;
    private final Options opts;
    private final Events events;
    private final Map<String, CompletableFuture<Outcome>> ended = new HashMap<>();   // filled once, then only read
    private final Map<Outcome, AtomicInteger> counts = new EnumMap<>(Outcome.class);
    private final CountDownLatch allEnded;
    private final CountDownLatch started = new CountDownLatch(1);
    private final BlockingQueue<Job> queue;
    private final Semaphore slots;
    private final Set<String> dispatched = ConcurrentHashMap.newKeySet();
    private final Map<String, Future<?>> tasks = new ConcurrentHashMap<>();
    private volatile Thread producer;
    private volatile Thread dispatcher;

    public Runner(List<Job> plan, Options opts, Events events) {
        this.plan = plan;
        this.opts = opts;
        this.events = events;
        for (Job job : plan) ended.put(job.id(), new CompletableFuture<>());
        for (Outcome o : Outcome.values()) counts.put(o, new AtomicInteger());
        this.allEnded = new CountDownLatch(plan.size());
        this.queue = new ArrayBlockingQueue<>(opts.queue());   // put() blocks when full: backpressure
        this.slots = new Semaphore(opts.workers());
    }

    public Map<Outcome, Integer> run() throws InterruptedException {
        try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
            producer = Thread.ofVirtual().name("producer").start(this::produce);
            dispatcher = Thread.ofVirtual().name("dispatcher").start(() -> dispatch(exec));
            started.countDown();
            allEnded.await();
            producer.join();
            dispatcher.join();
        }   // close() waits for every task and every attempt they submitted
        Map<Outcome, Integer> result = new EnumMap<>(Outcome.class);
        counts.forEach((o, n) -> result.put(o, n.get()));
        return result;
    }

    /**
     * Called on the shutdown hook's thread. Stops the producer and dispatcher, drops what was
     * never dispatched, gives running jobs {@code graceMs}, then cancels them.
     * False if there is nothing to drain.
     */
    public boolean requestStop() {
        if (!events.beginShutdown()) return false;
        try {
            started.await();
            producer.interrupt();
            dispatcher.interrupt();
            dispatcher.join();      // after this, no job is dispatched any more
            for (Job job : plan) {
                if (!dispatched.contains(job.id())) finish(job, Outcome.DROP, "drop " + job.id());
            }
            if (!allEnded.await(opts.graceMs(), TimeUnit.MILLISECONDS)) {
                tasks.values().forEach(task -> task.cancel(true));   // interrupts each job's thread
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return true;
    }

    private void produce() {
        try {
            for (Job job : plan) {
                queue.put(job);
                events.emit("queued " + job.id());
            }
            queue.put(END);
        } catch (InterruptedException e) {
            // shutdown: stop producing
        }
    }

    private void dispatch(ExecutorService exec) {
        try {
            while (true) {
                slots.acquire();            // a slot first, then a job: full slots stop the taking
                Job job;
                try {
                    job = queue.take();
                } catch (InterruptedException e) {
                    slots.release();
                    return;
                }
                if (job == END) {
                    slots.release();
                    return;
                }
                dispatched.add(job.id());
                tasks.put(job.id(), exec.submit(() -> {
                    try {
                        runJob(job, exec);
                    } finally {
                        slots.release();
                    }
                }));
            }
        } catch (InterruptedException e) {
            // shutdown: stop dispatching
        }
    }

    private void runJob(Job job, ExecutorService exec) {
        List<Outcome> depOutcomes = new ArrayList<>();
        try {
            for (String dep : job.deps()) depOutcomes.add(ended.get(dep).get());
        } catch (InterruptedException e) {
            finish(job, Outcome.DROP, "drop " + job.id());
            return;
        } catch (ExecutionException e) {
            throw new IllegalStateException("outcomes never complete exceptionally", e);
        }
        for (int i = 0; i < job.deps().size(); i++) {
            Outcome o = depOutcomes.get(i);
            if (o == Outcome.GIVEUP || o == Outcome.SKIP) {
                finish(job, Outcome.SKIP, "skip " + job.id() + " dep=" + job.deps().get(i));
                return;
            }
        }
        if (depOutcomes.stream().anyMatch(o -> o != Outcome.DONE)) {
            finish(job, Outcome.DROP, "drop " + job.id());
            return;
        }

        for (int attempt = 1; ; attempt++) {
            if (!events.startAttempt(job.id(), attempt)) {
                finish(job, Outcome.DROP, "drop " + job.id());
                return;
            }
            Attempt result;
            try {
                result = attempt(job, attempt, exec);
            } catch (InterruptedException e) {      // the grace period is over
                finish(job, Outcome.CANCEL, "cancel " + job.id() + " attempt=" + attempt);
                return;
            }
            if (result == Attempt.OK) {
                finish(job, Outcome.DONE, "done " + job.id() + " attempt=" + attempt);
                return;
            }
            events.emit((result == Attempt.FAILED ? "fail " : "timeout ") + job.id() + " attempt=" + attempt);
            if (attempt > opts.retries()) {
                finish(job, Outcome.GIVEUP, "giveup " + job.id() + " attempts=" + attempt);
                return;
            }
            long wait = Job.backoffMs(attempt);
            events.emit("retry " + job.id() + " attempt=" + (attempt + 1) + " delay=" + wait);
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                finish(job, Outcome.DROP, "drop " + job.id());
                return;
            }
        }
    }

    /** One attempt on its own virtual thread, so a timeout can cancel it. */
    private Attempt attempt(Job job, int attempt, ExecutorService exec) throws InterruptedException {
        Future<?> work = exec.submit(() -> {
            Thread.sleep(job.durationMs());         // the simulated work; interruptible
            if (attempt <= job.fail()) throw new IllegalStateException("simulated failure");
            return null;
        });
        try {
            if (opts.timeoutMs() > 0) work.get(opts.timeoutMs(), TimeUnit.MILLISECONDS);
            else work.get();
            return Attempt.OK;
        } catch (ExecutionException e) {
            return Attempt.FAILED;
        } catch (TimeoutException e) {
            return Attempt.TIMED_OUT;          // BUG: stops waiting, but the work keeps running
        } catch (InterruptedException e) {
            work.cancel(true);
            throw e;
        }
    }

    private void finish(Job job, Outcome outcome, String line) {
        events.emit(line);
        counts.get(outcome).incrementAndGet();
        ended.get(job.id()).complete(outcome);
        allEnded.countDown();
    }
}
