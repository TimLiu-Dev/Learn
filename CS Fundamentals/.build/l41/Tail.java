import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

// A local service where 2% of requests are slow. Measure 10,000 requests,
// then look at the distribution instead of the average.
public class Tail {
    public static void main(String[] args) throws Exception {
        var count = new AtomicInteger();
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/book", ex -> {
            if (count.incrementAndGet() % 50 == 7) {           // exactly 1 request in 50
                try { Thread.sleep(20); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
            byte[] body = "Dune".getBytes();
            ex.sendResponseHeaders(200, body.length);
            try (var out = ex.getResponseBody()) { out.write(body); }
        });
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();

        var client = HttpClient.newHttpClient();
        var req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/book")).build();
        for (int i = 0; i < 500; i++) client.send(req, HttpResponse.BodyHandlers.discarding());  // warm-up
        count.set(0);

        double[] ms = new double[10_000];
        for (int i = 0; i < ms.length; i++) {
            long t = System.nanoTime();
            client.send(req, HttpResponse.BodyHandlers.discarding());
            ms[i] = (System.nanoTime() - t) / 1e6;
        }
        server.stop(0);

        double[] sorted = ms.clone();
        Arrays.sort(sorted);                                    // L18: sort, then index
        double mean = Arrays.stream(ms).average().orElseThrow();
        System.out.printf("requests %d, slow %.0f%%%n", ms.length, 100.0 * Arrays.stream(ms).filter(x -> x > 10).count() / ms.length);
        System.out.printf("mean   %6.2f ms%n", mean);
        for (double p : new double[] {50, 95, 99, 99.9}) System.out.printf("p%-5s %6.2f ms%n", fmt(p), pct(sorted, p));
        System.out.printf("max    %6.2f ms%n", sorted[sorted.length - 1]);

        // Fan-out: a page calls 10 services, each with this distribution.
        double p99 = pct(sorted, 99);
        var rnd = new Random(41);
        int pages = 100_000, hit = 0;
        for (int p = 0; p < pages; p++) {
            boolean anySlow = false;
            for (int call = 0; call < 10; call++) if (ms[rnd.nextInt(ms.length)] > p99) anySlow = true;
            if (anySlow) hit++;
        }
        System.out.printf("pages with a call slower than p99: computed %.1f%%, simulated %.1f%%%n",
                100 * (1 - Math.pow(0.99, 10)), 100.0 * hit / pages);
    }

    // Nearest-rank percentile: the value at position ceil(p/100 * n) in sorted order
    static double pct(double[] sorted, double p) {
        int rank = (int) Math.ceil(p / 100 * sorted.length);
        return sorted[Math.max(0, rank - 1)];
    }

    static String fmt(double p) { return p == Math.rint(p) ? String.valueOf((int) p) : String.valueOf(p); }
}
