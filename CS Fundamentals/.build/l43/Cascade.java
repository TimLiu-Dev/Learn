import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// A dependency with one hung endpoint (/slow sleeps 5 s) and one healthy one (/fast).
// Our service has a pool of 8 worker threads. 50 requests need /slow, then 1 needs /fast.
public class Cascade {
    static final HttpClient client = HttpClient.newHttpClient();

    static String call(int port, String path, Duration timeout) throws Exception {
        var b = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path));
        if (timeout != null) b.timeout(timeout);                      // no timeout = wait forever
        try { return client.send(b.build(), HttpResponse.BodyHandlers.ofString()).body(); }
        catch (HttpTimeoutException e) { return "timeout"; }
    }

    static void run(String label, int port, Duration timeout, boolean bulkhead) throws Exception {
        ExecutorService slowPool = Executors.newFixedThreadPool(8);
        ExecutorService fastPool = bulkhead ? Executors.newFixedThreadPool(2) : slowPool;
        AtomicInteger done = new AtomicInteger(), timedOut = new AtomicInteger();
        long start = System.nanoTime();
        for (int i = 0; i < 50; i++) {
            slowPool.submit(() -> { if (call(port, "/slow", timeout).equals("timeout")) timedOut.incrementAndGet(); done.incrementAndGet(); return null; });
        }
        Future<Long> fast = fastPool.submit(() -> { call(port, "/fast", timeout); return (System.nanoTime() - start) / 1_000_000; });
        String fastMs;
        try { fastMs = "answered after " + fast.get(2500, TimeUnit.MILLISECONDS) + " ms"; }
        catch (TimeoutException e) { fastMs = "still waiting after 2500 ms"; }
        slowPool.shutdown();
        boolean finished = slowPool.awaitTermination(3, TimeUnit.SECONDS);
        System.out.println(label);
        System.out.println("  /fast request: " + fastMs);
        System.out.println("  /slow requests: " + done.get() + " of 50 finished (" + timedOut.get() + " timed out)"
                + (finished ? " after " + (System.nanoTime() - start) / 1_000_000 + " ms" : ", the rest still stuck 3 s later"));
        slowPool.shutdownNow(); fastPool.shutdownNow();
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.createContext("/slow", ex -> {
            try { Thread.sleep(5000); } catch (InterruptedException e) { return; }
            ex.sendResponseHeaders(200, 2); try (OutputStream o = ex.getResponseBody()) { o.write("ok".getBytes()); }
        });
        server.createContext("/fast", ex -> { ex.sendResponseHeaders(200, 2); try (OutputStream o = ex.getResponseBody()) { o.write("ok".getBytes()); } });
        server.start();
        int port = server.getAddress().getPort();
        run("1) one shared pool, no timeouts", port, null, false);
        run("2) one shared pool, 300 ms timeouts", port, Duration.ofMillis(300), false);
        run("3) separate pools (bulkhead), no timeouts", port, null, true);
        server.stop(0);
        System.exit(0);                                   // abandoned /slow handlers would otherwise linger
    }
}
