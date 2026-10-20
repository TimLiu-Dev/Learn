import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

// The same workload on virtual threads (L28): plain blocking code
public class Million {
    public static void main(String[] args) throws Exception {
        int n = Integer.getInteger("n", 100_000);       // try -Dn=10000
        var rt = Runtime.getRuntime();
        var peak = new AtomicLong();
        var running = new java.util.concurrent.atomic.AtomicBoolean(true);
        Thread sampler = Thread.ofPlatform().daemon().start(() -> {
            while (running.get()) {
                peak.accumulateAndGet(rt.totalMemory() - rt.freeMemory(), Math::max);
                sleep(5);
            }
        });
        long t0 = System.nanoTime();
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < n; i++) exec.submit(() -> sleep(1000));
        }                                         // close() waits for all
        long ms = (System.nanoTime() - t0) / 1_000_000;
        running.set(false); sampler.join();
        System.out.println(n + " virtual threads x sleep(1000): " + ms
            + " ms, peak heap ~" + peak.get() / (1024 * 1024) + " MB");
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
