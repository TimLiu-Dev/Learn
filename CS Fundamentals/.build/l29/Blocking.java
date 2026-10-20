import java.util.concurrent.*;

// Nearest equivalent: a fixed CPU-sized pool, starved by blocking calls
public class Blocking {
    public static void main(String[] args) throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        for (boolean fix : new boolean[] {false, true}) {
            try (var cpu = Executors.newFixedThreadPool(cores);
                 var io = Executors.newVirtualThreadPerTaskExecutor()) {
                long t0 = System.nanoTime();
                for (int i = 0; i < cores; i++) {
                    (fix ? io : cpu).submit(() -> sleep(1000));   // the blocking calls
                }
                Thread.sleep(50);
                cpu.submit(() -> {                                 // a tiny, urgent job
                    long ms = (System.nanoTime() - t0) / 1_000_000;
                    System.out.println(fix ? "io pool fix: quick job ran at " + ms + " ms"
                                           : "blocking:    quick job ran at " + ms + " ms");
                }).get();
            }
        }
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
