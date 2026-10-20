import java.util.concurrent.*;

// Nearest equivalent: a single-thread "UI" executor, an I/O pool,
// and CompletableFuture to hop between them.
public class MainThread {
    static long t0;
    static void log(String msg) {
        System.out.printf("%5d ms [%s] %s%n", (System.nanoTime() - t0) / 1_000_000,
            Thread.currentThread().getName(), msg);
    }

    static String loadReport() { sleep(400); return "42 rows"; }   // blocking I/O

    static void screen(boolean blockTheUi) throws Exception {
        t0 = System.nanoTime();
        try (var ui = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "ui"));
             var io = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 6; i++) {                  // the UI keeps drawing
                int n = i;
                ui.schedule(() -> log("frame " + n), 100L * i, TimeUnit.MILLISECONDS);
            }
            ui.schedule(() -> {                            // a button click
                log("click");
                if (blockTheUi) {
                    log("show " + loadReport());           // BAD: on the UI thread
                } else {
                    CompletableFuture.supplyAsync(MainThread::loadReport, io)
                        .thenAcceptAsync(r -> log("show " + r), ui);  // back to ui
                }
            }, 150, TimeUnit.MILLISECONDS);
            Thread.sleep(700);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("== blocking the UI thread"); screen(true);
        System.out.println("== supplyAsync(io) + thenAcceptAsync(ui)"); screen(false);
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
