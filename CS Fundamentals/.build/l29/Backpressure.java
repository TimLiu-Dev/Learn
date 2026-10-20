import java.util.concurrent.*;

// Nearest equivalent: SubmissionPublisher's bounded buffer.
// submit() blocks when the buffer is full; offer() can drop instead.
public class Backpressure {
    static long run(boolean dropWhenFull) throws Exception {
        var done = new CountDownLatch(1);
        long t0 = System.nanoTime();
        try (var pub = new SubmissionPublisher<Integer>(ForkJoinPool.commonPool(), 1)) {
            pub.consume(i -> { sleep(300); System.out.print(i + " "); })
               .thenRun(done::countDown);
            for (int i = 1; i <= 6; i++) {
                sleep(100);
                if (dropWhenFull) pub.offer(i, (sub, item) -> false);  // drop it
                else pub.submit(i);                                    // wait for room
            }
        }
        done.await();
        return (System.nanoTime() - t0) / 1_000_000;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(" <- submit (waits): " + run(false) + " ms");
        System.out.println(" <- offer (drops):  " + run(true) + " ms");
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
