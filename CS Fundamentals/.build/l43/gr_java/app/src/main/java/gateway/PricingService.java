package gateway;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.Executors;
import static gateway.Telemetry.*;

/**
 * A deliberately flaky pricing service: 20% of calls fail with 500, 5% hang for 2 s.
 * Between outageFrom and outageTo (ms since start) every call fails with 503, to trip the breaker.
 */
public final class PricingService {
    private final Random random;
    private final long start = System.nanoTime();
    public final HttpServer server;

    public PricingService(long seed, long outageFrom, long outageTo) throws IOException {
        random = new Random(seed);
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.createContext("/price", ex -> {
            long begin = Tracer.nowMs();
            String[] tp = parseTraceparent(ex.getRequestHeaders().getFirst("traceparent"));
            String traceId = tp != null ? tp[0] : randomHex(16), parentId = tp != null ? tp[1] : null;
            String path = ex.getRequestURI().getPath();
            int id;
            try { id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1)); } catch (NumberFormatException e) { id = 0; }
            int roll; synchronized (random) { roll = random.nextInt(100); }
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            int status;
            if (elapsed >= outageFrom && elapsed < outageTo) status = 503;
            else if (roll < 20) status = 500;
            else if (roll < 25) { try { Thread.sleep(2000); } catch (InterruptedException e) { return; } status = 200; }
            else status = 200;
            String body = status == 200 ? String.format("%.2f", 10.0 + id % 7) : "error";
            Tracer.record(new Span("pricing", "GET " + ex.getRequestURI(), traceId, randomHex(8), parentId, begin, Tracer.nowMs() - begin, Integer.toString(status)));
            byte[] bytes = body.getBytes();
            ex.sendResponseHeaders(status, bytes.length);
            try (OutputStream o = ex.getResponseBody()) { o.write(bytes); }
        });
    }
}
