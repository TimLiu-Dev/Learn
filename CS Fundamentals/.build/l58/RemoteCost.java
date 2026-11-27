import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class RemoteCost {
    static final class Catalogue {
        String titleOf(String barcode) { return barcode.equals("C-001") ? "Kindred" : "unknown"; }
    }

    // Median time of one call, in nanoseconds, over n calls.
    static long medianNanos(int n, Callable<String> call) throws Exception {
        int sink = 0;                                       // use every result, so the JIT can't drop the call (L16)
        long[] times = new long[n];
        for (int i = 0; i < n; i++) {
            long start = System.nanoTime();
            sink += call.call().length();
            times[i] = System.nanoTime() - start;
        }
        if (sink == 0) throw new AssertionError();
        Arrays.sort(times);
        return times[n / 2];
    }

    public static void main(String[] args) throws Exception {
        Catalogue catalogue = new Catalogue();
        // The same catalogue, behind HTTP on this machine: the smallest "microservice" there is.
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/title", exchange -> {
            byte[] body = catalogue.titleOf(exchange.getRequestURI().getQuery()).getBytes();
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) { out.write(body); }
        });
        server.start();
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/title?C-001");
        Callable<String> remote = () -> client.send(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString()).body();

        medianNanos(5_000, () -> catalogue.titleOf("C-001"));   // warm-up for both (L16)
        medianNanos(2_000, remote);
        long local = medianNanos(5_000, () -> catalogue.titleOf("C-001"));
        long http = medianNanos(2_000, remote);
        System.out.println("same answer?      " + catalogue.titleOf("C-001").equals(remote.call()));
        System.out.println("in-process call:  " + local + " ns");
        System.out.println("HTTP, localhost:  " + http / 1_000 + " µs  (about " + http / Math.max(local, 1) + " times slower)");
        server.stop(0);
        client.close();
    }
}
