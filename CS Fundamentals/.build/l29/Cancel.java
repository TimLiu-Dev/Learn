import java.util.concurrent.*;

// Nearest equivalent: thread interruption is cooperative too.
public class Cancel {
    static void busyFor(long ms, Runnable check) {
        long end = System.currentTimeMillis() + ms;
        while (System.currentTimeMillis() < end) check.run();
    }

    public static void main(String[] args) throws Exception {
        Thread stubborn = Thread.ofVirtual().start(() -> {
            busyFor(500, () -> { });               // never checks
            System.out.println("stubborn: loop ran to the end anyway");
        });
        Thread.sleep(50);
        long t0 = System.nanoTime();
        stubborn.interrupt();
        stubborn.join();
        System.out.println("stubborn: interrupt+join took " + ms(t0) + " ms");

        Thread polite = Thread.ofVirtual().start(() -> {
            try {
                busyFor(500, () -> {
                    if (Thread.currentThread().isInterrupted())
                        throw new CancellationException();
                });
                System.out.println("polite: never printed");
            } catch (CancellationException e) { /* stopped early */ }
        });
        Thread.sleep(50);
        t0 = System.nanoTime();
        polite.interrupt();
        polite.join();
        System.out.println("polite: interrupt+join took " + ms(t0) + " ms");

        var f = CompletableFuture.supplyAsync(() -> { sleep(1000); return "done"; })
            .completeOnTimeout(null, 200, TimeUnit.MILLISECONDS);
        System.out.println("completeOnTimeout -> " + f.join());
        // note: the sleeping task itself was NOT stopped
    }

    static long ms(long t0) { return (System.nanoTime() - t0) / 1_000_000; }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
