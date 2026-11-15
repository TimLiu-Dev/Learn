import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Practice {
    // Caches loader's results. loader must run at most once per key.
    static class Memo {
        private final Function<String, String> loader;
        private final Map<String, String> values = new HashMap<>();
        private int loads = 0;                       // how many times loader ran

        Memo(Function<String, String> loader) { this.loader = loader; }

        // One lock (this) around the whole check-then-act, so no thread can slip in between.
        synchronized String get(String key) {
            return values.computeIfAbsent(key, k -> {
                loads++;
                return loader.apply(k);
            });
        }

        synchronized int loads() { return loads; }  // reads use the same lock as writes
    }

    // CHECKS
    // True once thread t is stuck at a lock that the calling thread holds.
    static boolean waitsForMe(Thread t) {
        ThreadInfo info = ManagementFactory.getThreadMXBean().getThreadInfo(t.threadId());
        return info != null && info.getLockOwnerId() == Thread.currentThread().threadId();
    }

    static Object midLoad() throws InterruptedException {
        AtomicInteger calls = new AtomicInteger();
        Memo[] memo = new Memo[1];
        Thread[] second = new Thread[1];
        memo[0] = new Memo(key -> {
            if (calls.incrementAndGet() == 1) {
                // First load: start a second thread that wants the same key, and wait
                // until it either loads the key too (the race) or stops at a lock.
                second[0] = new Thread(() -> memo[0].get(key));
                second[0].start();
                while (calls.get() == 1 && second[0].isAlive() && !waitsForMe(second[0])) Thread.onSpinWait();
            }
            return "value of " + key;
        });
        String got = memo[0].get("k");
        second[0].join();
        return List.of(got, calls.get(), memo[0].loads());
    }

    static Object manyThreads() throws InterruptedException {
        for (int round = 0; round < 5; round++) {
            AtomicInteger calls = new AtomicInteger();
            Memo memo = new Memo(k -> { calls.incrementAndGet(); return "v" + k; });
            CountDownLatch gate = new CountDownLatch(1);       // hold all threads at the start line
            AtomicInteger wrong = new AtomicInteger();
            Thread[] threads = new Thread[8];
            for (int t = 0; t < 8; t++) {
                int start = t * 25;                             // each thread starts at a different key
                threads[t] = new Thread(() -> {
                    try { gate.await(); } catch (InterruptedException e) { return; }
                    for (int i = 0; i < 200; i++) {
                        String key = "k" + (i + start) % 200;
                        if (!memo.get(key).equals("v" + key)) wrong.incrementAndGet();
                    }
                });
                threads[t].start();
            }
            gate.countDown();
            for (Thread t : threads) t.join();
            List<Integer> result = List.of(calls.get(), memo.loads(), wrong.get());
            if (!result.equals(List.of(200, 200, 0))) return "round " + round + ": calls, loads, wrong = " + result;
        }
        return "every round: calls, loads, wrong = [200, 200, 0]";
    }

    public static void main(String[] args) {
        check("one thread loads each key once", List.of("A", "A", "B", 2), () -> {
            Memo memo = new Memo(String::toUpperCase);
            return List.of(memo.get("a"), memo.get("a"), memo.get("b"), memo.loads());
        });
        check("a thread that asks mid-load waits instead of loading again (value, loader calls, loads)",
            List.of("value of k", 1, 1), Practice::midLoad);
        check("8 threads, 200 keys, every key loaded exactly once",
            "every round: calls, loads, wrong = [200, 200, 0]", Practice::manyThreads);
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
