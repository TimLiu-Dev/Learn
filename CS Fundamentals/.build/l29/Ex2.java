import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Ex2 {
    static final AtomicInteger running = new AtomicInteger(), maxRunning = new AtomicInteger();
    static final Semaphore network = new Semaphore(3);           // at most 3 at once

    static String download(int id) {                             // blocking, 200 ms
        try {
            network.acquire();
            try {
                maxRunning.accumulateAndGet(running.incrementAndGet(), Math::max);
                Thread.sleep(200);
                running.decrementAndGet();
                return "file" + id;
            } finally { network.release(); }
        } catch (InterruptedException e) { throw new CancellationException(); }
    }

    public static void main(String[] args) {
        long t0 = System.nanoTime();
        var done = new AtomicInteger();
        try (var ui = Executors.newSingleThreadExecutor(r -> new Thread(r, "ui"));
             var io = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<String>> fs = IntStream.rangeClosed(1, 9).boxed()
                .map(id -> CompletableFuture.supplyAsync(() -> download(id), io)
                    .thenApplyAsync(f -> {                          // progress on "ui"
                        System.out.print("[" + Thread.currentThread().getName() + " "
                            + done.incrementAndGet() + "/9] ");
                        return f;
                    }, ui))
                .toList();
            List<String> files = fs.stream().map(CompletableFuture::join).toList();
            System.out.println();
            System.out.println(files.size() + " files, max " + maxRunning.get() + " at once, "
                + (System.nanoTime() - t0) / 1_000_000 + " ms");
        }
    }
}
