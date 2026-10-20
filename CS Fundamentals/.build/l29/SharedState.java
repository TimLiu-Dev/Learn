import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

// Nearest equivalent: 1000 virtual threads x 1000 increments (see L26)
public class SharedState {
    static int plain, locked, confined;

    static long massiveRun(ExecutorService exec, Runnable action) {
        long t0 = System.nanoTime();
        var futures = new java.util.ArrayList<Future<?>>();
        for (int i = 0; i < 1000; i++) {
            futures.add(exec.submit(() -> { for (int j = 0; j < 1000; j++) action.run(); }));
        }
        for (var f : futures) {
            try { f.get(); } catch (Exception e) { throw new RuntimeException(e); }
        }
        return (System.nanoTime() - t0) / 1_000_000;
    }

    public static void main(String[] args) {
        try (var vt = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int run = 1; run <= 3; run++) {
                plain = 0;
                massiveRun(vt, () -> plain++);
                System.out.println("plain field, run " + run + ": " + plain);
            }

            var lock = new ReentrantLock();
            long ms = massiveRun(vt, () -> { lock.lock(); try { locked++; } finally { lock.unlock(); } });
            System.out.println("ReentrantLock:     " + locked + "  (" + ms + " ms)");

            var atomic = new AtomicInteger();
            ms = massiveRun(vt, atomic::incrementAndGet);
            System.out.println("AtomicInteger:     " + atomic.get() + "  (" + ms + " ms)");
        }
        try (var one = Executors.newSingleThreadExecutor()) {
            long ms = massiveRun(one, () -> confined++);
            System.out.println("confined:          " + confined + "  (" + ms + " ms)");
        }
    }
}
