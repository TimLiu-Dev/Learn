import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Practice {
    // Lets at most `permits` callers run a block at the same time.
    static class Gate {
        private final Semaphore semaphore;

        Gate(int permits) { semaphore = new Semaphore(permits); }

        int available() { return semaphore.availablePermits(); }

        <T> T enter(Callable<T> block) throws Exception {
            semaphore.acquire();                // waits here while every permit is taken
            try {
                return block.call();
            } finally {
                semaphore.release();            // runs on return and on throw
            }
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("enter returns the block's value and gives the permit back", "42, 2 permits", () -> {
            Gate gate = new Gate(2);
            int v = gate.enter(() -> 42);
            return v + ", " + gate.available() + " permits";
        });
        check("while a block runs, its permit is taken", 1, () -> {
            Gate gate = new Gate(2);
            return gate.enter(gate::available);
        });
        check("a failing block still gives its permit back", "query failed, 1 permits", () -> {
            Gate gate = new Gate(1);
            try {
                gate.enter(() -> { throw new IllegalStateException("query failed"); });
                return "no exception";
            } catch (IllegalStateException e) {
                return e.getMessage() + ", " + gate.available() + " permits";
            }
        });
        check("100 virtual threads, never more than 3 inside", "sum 5050, max ok, 3 permits", () -> {
            Gate gate = new Gate(3);
            AtomicInteger inside = new AtomicInteger(), maxSeen = new AtomicInteger();
            List<Future<Integer>> futures = new ArrayList<>();
            try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {   // JDK 21+
                for (int i = 1; i <= 100; i++) {
                    int id = i;
                    futures.add(ex.submit(() -> gate.enter(() -> {
                        maxSeen.accumulateAndGet(inside.incrementAndGet(), Math::max);
                        Thread.sleep(5);                    // stay inside a moment, so callers overlap
                        inside.decrementAndGet();
                        return id;
                    })));
                }
            }
            int sum = 0;
            for (Future<Integer> f : futures) sum += f.get();
            return "sum " + sum + ", max " + (maxSeen.get() <= 3 ? "ok" : maxSeen.get() + " > 3") + ", " + gate.available() + " permits";
        });
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
