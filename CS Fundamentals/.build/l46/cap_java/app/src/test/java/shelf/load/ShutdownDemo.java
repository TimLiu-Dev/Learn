package shelf.load;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import shelf.Config;
import shelf.Shelf;
import shelf.store.Db;
import shelf.store.SqliteBookStore;

// Graceful shutdown, for real: three slow requests are in flight when close() is called.
public final class ShutdownDemo {
    public static void main(String[] args) throws Exception {
        for (int drain : new int[] {0, 5}) {                     // v1 called stop(0); v2 drains for up to 5 s
            System.out.println("drainSeconds = " + drain);
            demo(drain);
        }
    }

    private static void demo(int drain) throws Exception {
        Path file = Files.createTempFile("shelf-stop", ".db");
        try (Db db = new Db(file)) {
            db.createSchema();
            new SqliteBookStore(db).add("Dune", "Frank Herbert", 1965);
        }
        long t0 = System.nanoTime();
        Shelf shelf = new Shelf(Config.defaults().withPort(0).withDbFile(file).withCache(0).withDrain(drain),
                                Clock.systemUTC(), line -> {}, b -> new SlowBookStore(b, 800)).start();   // every read takes 0.8 s
        int port = shelf.port();
        HttpClient client = HttpClient.newHttpClient();
        List<CompletableFuture<Void>> inFlight = new ArrayList<>();
        for (int n = 1; n <= 3; n++) {
            int k = n;
            inFlight.add(client.sendAsync(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/books/1")).build(),
                                          HttpResponse.BodyHandlers.ofString())
                .handle((r, e) -> {
                    System.out.println(at(t0) + "  request " + k + (r != null
                        ? " finished: " + r.statusCode() + " " + r.body()
                        : " FAILED: " + e.getCause().getClass().getSimpleName()));
                    return null;
                }));
        }
        Thread.sleep(200);
        System.out.println(at(t0) + "  close() called with 3 requests in flight");
        shelf.close();
        System.out.println(at(t0) + "  close() returned");
        inFlight.forEach(CompletableFuture::join);
        try {
            client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/health")).build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println(at(t0) + "  new request after close: " + e.getClass().getSimpleName());
        }
        Files.deleteIfExists(file);
    }

    private static String at(long t0) {
        return String.format("%4d ms", (System.nanoTime() - t0) / 1_000_000);
    }
}
