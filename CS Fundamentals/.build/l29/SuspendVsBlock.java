import java.util.concurrent.*;

// Java has no coroutines. Nearest equivalent on ONE thread:
// schedule callbacks instead of blocking.
public class SuspendVsBlock {
    public static void main(String[] args) throws Exception {
        try (var one = Executors.newSingleThreadScheduledExecutor()) {
            var done = new CountDownLatch(10_000);
            long t0 = System.nanoTime();
            for (int i = 0; i < 10_000; i++) {
                one.schedule(done::countDown, 100, TimeUnit.MILLISECONDS);
            }
            done.await();
            System.out.println("10,000 callbacks after 100ms: " + ms(t0) + "ms");

            var slept = new CountDownLatch(20);
            t0 = System.nanoTime();
            for (int i = 0; i < 20; i++) {
                one.execute(() -> { sleep(100); slept.countDown(); });
            }
            slept.await();
            System.out.println("20 x Thread.sleep(100):       " + ms(t0) + "ms");
        }
    }

    static long ms(long t0) { return (System.nanoTime() - t0) / 1_000_000; }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
