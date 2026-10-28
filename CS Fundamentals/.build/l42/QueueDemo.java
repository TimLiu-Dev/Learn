import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QueueDemo {
    static final long t0 = System.currentTimeMillis();

    static synchronized void log(String msg) {
        System.out.printf("  [%4d ms] %s%n", System.currentTimeMillis() - t0, msg);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("-- at-least-once: worker w2 crashes after sending an email, before acking it");
        emails(false);
        System.out.println("-- the same crash, with an idempotent consumer");
        emails(true);
        System.out.println("-- a poison message");
        poison();
        System.out.println("-- backpressure: a queue of 5, a producer faster than its consumer");
        backpressure();
    }

    static void emails(boolean idempotent) throws Exception {
        try (var q = new JobQueue(100, 300, 3)) {
            for (int i = 1; i <= 6; i++) q.push("welcome email for user " + i);
            List<Long> sent = new CopyOnWriteArrayList<>();               // the outside world: emails really sent
            Set<Long> done = ConcurrentHashMap.newKeySet();               // stands in for a processed_ids table
            var crashed = new AtomicBoolean(false);
            List<Thread> workers = IntStream.rangeClosed(1, 3).mapToObj(w -> Thread.ofVirtual().start(() -> {
                try {
                    while (true) {
                        var m = q.receive(500);
                        if (m == null) break;
                        if (idempotent && done.contains(m.id())) { log("w" + w + " skips #" + m.id() + ": already done"); q.ack(m.id()); continue; }
                        sent.add(m.id());
                        log("w" + w + " sends #" + m.id() + " (attempt " + m.attempts() + ")");
                        if (idempotent) done.add(m.id());
                        if (w == 2 && crashed.compareAndSet(false, true)) { log("w2 CRASHES before acking #" + m.id()); break; }
                        q.ack(m.id());
                    }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            })).toList();
            for (Thread t : workers) t.join();
            Map<Long, Long> counts = sent.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            List<Long> dupes = counts.entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).toList();
            System.out.println("  6 jobs -> " + sent.size() + " emails sent, duplicates: " + dupes);
        }
    }

    static void poison() throws Exception {
        try (var q = new JobQueue(100, 100, 3)) {
            for (String body : List.of("ok 1", "BAD", "ok 2")) q.push(body);
            while (true) {
                var m = q.receive(400);
                if (m == null) break;
                if (m.body().equals("BAD")) {
                    log("#" + m.id() + " attempt " + m.attempts() + " failed: cannot parse '" + m.body() + "'");  // no ack
                } else {
                    log("#" + m.id() + " '" + m.body() + "' done");
                    q.ack(m.id());
                }
            }
            System.out.println("  dead letters: " + q.deadLetters);
        }
    }

    static void backpressure() throws Exception {
        try (var q = new JobQueue(5, 1000, 3)) {
            Thread consumer = Thread.ofVirtual().start(() -> {
                try {
                    while (true) { var m = q.receive(300); if (m == null) break; Thread.sleep(50); q.ack(m.id()); }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
            long start = System.currentTimeMillis();
            for (int i = 0; i < 20; i++) q.push("job " + i);                // blocks whenever 5 are waiting
            System.out.println("  push(): 20 jobs queued after " + (System.currentTimeMillis() - start) + " ms (the producer was slowed down)");
            long rejected = IntStream.rangeClosed(1, 20).filter(i -> !q.offer("extra " + i)).count();  // never blocks
            System.out.println("  offer(): " + rejected + " of 20 extra jobs rejected straight away");
            consumer.join();
        }
    }
}
