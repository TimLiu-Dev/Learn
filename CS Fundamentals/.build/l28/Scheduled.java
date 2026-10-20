import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Scheduled {
    static final long START = System.nanoTime();
    static String stamp() { return String.format("%4d ms", (System.nanoTime() - START) / 1_000_000); }

    public static void main(String[] args) throws InterruptedException {
        // a ThreadFactory that names its threads: "ticker-0", "ticker-1", ...
        ThreadFactory factory = Thread.ofPlatform().name("ticker-", 0).factory();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, factory);

        scheduler.schedule(() -> System.out.println(stamp() + " one-off reminder on "
                + Thread.currentThread().getName()), 250, TimeUnit.MILLISECONDS);
        ScheduledFuture<?> ticks = scheduler.scheduleAtFixedRate(
                () -> System.out.println(stamp() + " tick"), 0, 100, TimeUnit.MILLISECONDS);

        Thread.sleep(450);
        ticks.cancel(false);                    // stop the repeating task
        scheduler.shutdown();
        System.out.println(stamp() + " terminated: " + scheduler.awaitTermination(1, TimeUnit.SECONDS));
    }
}
