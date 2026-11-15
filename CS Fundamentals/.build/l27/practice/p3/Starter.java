import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Practice {
    // A bounded blocking queue, like ArrayBlockingQueue: put waits while full, take waits while empty.
    static final class BoundedQueue<T> {
        private final ArrayDeque<T> items = new ArrayDeque<>();
        private final int capacity;
        final ReentrantLock lock = new ReentrantLock();
        final Condition notFull = lock.newCondition();       // put() waits here
        final Condition notEmpty = lock.newCondition();      // take() waits here

        BoundedQueue(int capacity) { this.capacity = capacity; }

        int size() {
            lock.lock();
            try { return items.size(); } finally { lock.unlock(); }
        }

        void put(T item) throws InterruptedException {
            throw new UnsupportedOperationException("TODO: while full, wait on notFull; add; wake the takers");
        }

        T take() throws InterruptedException {
            throw new UnsupportedOperationException("TODO: while empty, wait on notEmpty; remove the first; wake the putters");
        }
    }

    // CHECKS
    static final List<Throwable> errors = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        check("items come out in the order they went in", "[1, 2, 3] size 0", () -> {
            var q = new BoundedQueue<Integer>(3);
            q.put(1); q.put(2); q.put(3);
            return List.of(q.take(), q.take(), q.take()) + " size " + q.size();
        });
        check("take waits until something is put", "job", () -> {
            errors.clear();
            var q = new BoundedQueue<String>(2);
            String[] got = new String[1];
            Thread consumer = spawn(() -> got[0] = q.take());
            awaitParked(q, q.notEmpty, 1, List.of(consumer), "take() on an empty queue");
            q.put("job");
            consumer.join(2000);
            return got[0];
        });
        check("put waits while the queue is full", "size 2, then a b c", () -> {
            errors.clear();
            var q = new BoundedQueue<String>(2);
            q.put("a"); q.put("b");
            Thread producer = spawn(() -> q.put("c"));
            awaitParked(q, q.notFull, 1, List.of(producer), "put() on a full queue");
            int size = q.size();
            String first = q.take();
            producer.join(2000);
            return "size " + size + ", then " + first + " " + q.take() + " " + q.take();
        });
        check("a woken taker checks again before taking", "[x, y], no errors", () -> {
            errors.clear();
            var q = new BoundedQueue<String>(2);
            List<String> got = new CopyOnWriteArrayList<>();
            List<Thread> consumers = List.of(spawn(() -> got.add(q.take())), spawn(() -> got.add(q.take())));
            awaitParked(q, q.notEmpty, 2, consumers, "take() on an empty queue");
            q.put("x");                                    // wakes both takers: only one can have it
            long deadline = System.nanoTime() + 5_000_000_000L;
            while (got.size() + errors.size() < 2 && !(got.size() == 1 && waitingOn(q, q.notEmpty) == 1)) {
                if (System.nanoTime() > deadline) return "the other taker never went back to waiting";
                Thread.sleep(1);
            }
            q.put("y");
            for (Thread t : consumers) t.join(2000);
            return new TreeSet<>(got) + ", " + (errors.isEmpty() ? "no errors" : "errors: " + errors);
        });
        check("two producers and two consumers move 1,000 items exactly once", "1000 distinct, sum 500500", () -> {
            errors.clear();
            var q = new BoundedQueue<Integer>(4);
            var sum = new AtomicLong();
            Set<Integer> seen = ConcurrentHashMap.newKeySet();
            List<Thread> all = new ArrayList<>();
            for (int start : new int[] {1, 501}) all.add(spawn(() -> { for (int i = start; i < start + 500; i++) q.put(i); }));
            for (int c = 0; c < 2; c++) all.add(spawn(() -> { for (int i = 0; i < 500; i++) { int n = q.take(); sum.addAndGet(n); seen.add(n); } }));
            for (Thread t : all) t.join(5000);
            if (all.stream().anyMatch(Thread::isAlive)) return "a thread is still stuck";
            return seen.size() + " distinct, sum " + sum.get() + (errors.isEmpty() ? "" : ", errors: " + errors);
        });
    }

    interface Body { void run() throws Exception; }

    // A daemon thread that records anything it throws, so a failure shows up in the check.
    static Thread spawn(Body body) {
        Thread t = new Thread(() -> { try { body.run(); } catch (Throwable e) { errors.add(e); } });
        t.setDaemon(true);
        t.start();
        return t;
    }

    static int waitingOn(BoundedQueue<?> q, Condition c) {
        q.lock.lock();
        try { return q.lock.getWaitQueueLength(c); } finally { q.lock.unlock(); }
    }

    // Polls until n threads are parked in await() on c.
    static void awaitParked(BoundedQueue<?> q, Condition c, int n, List<Thread> threads, String what) throws Exception {
        long deadline = System.nanoTime() + 5_000_000_000L;
        while (waitingOn(q, c) < n) {
            if (threads.stream().anyMatch(t -> !t.isAlive())) throw new IllegalStateException(what + " returned instead of waiting. Errors: " + errors);
            if (System.nanoTime() > deadline) throw new IllegalStateException(what + " should be waiting on its condition (await), not spinning");
            Thread.sleep(1);
        }
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
