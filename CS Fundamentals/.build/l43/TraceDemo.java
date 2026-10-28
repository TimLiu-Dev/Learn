import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.function.Function;

// Three services in one JVM: gateway -> books -> pricing.
// Each one reads the W3C `traceparent` header, starts its own span,
// passes the trace on, and writes one structured (JSON) log line per span.
public class TraceDemo {
    record Span(String service, String name, String traceId, String spanId, String parentId, long startMs, long durMs) {}

    static final Queue<Span> spans = new ConcurrentLinkedQueue<>();  // stand-in for a trace collector
    static final HttpClient client = HttpClient.newHttpClient();
    static final SecureRandom random = new SecureRandom();
    static final long t0 = System.nanoTime();
    static long nowMs() { return (System.nanoTime() - t0) / 1_000_000; }
    static String hex(int bytes) { byte[] b = new byte[bytes]; random.nextBytes(b); return HexFormat.of().formatHex(b); }

    static void traced(String service, HttpExchange ex, Function<String, String> work) throws IOException {
        // traceparent = version "-" trace-id (32 hex) "-" parent-id (16 hex) "-" flags
        String header = ex.getRequestHeaders().getFirst("traceparent");
        String[] incoming = header == null ? null : header.split("-");
        String traceId = incoming != null ? incoming[1] : hex(16);      // no header: we start a new trace
        String parentId = incoming != null ? incoming[2] : null;
        String spanId = hex(8);
        long start = nowMs();
        String body = work.apply("00-" + traceId + "-" + spanId + "-01"); // children see *our* span as their parent
        long dur = nowMs() - start;
        spans.add(new Span(service, ex.getRequestMethod() + " " + ex.getRequestURI(), traceId, spanId, parentId, start, dur));
        System.out.println("{\"level\":\"INFO\",\"service\":\"" + service + "\",\"msg\":\"" + ex.getRequestMethod() + " " + ex.getRequestURI()
                + "\",\"status\":200,\"duration_ms\":" + dur + ",\"trace_id\":\"" + traceId + "\",\"span_id\":\"" + spanId
                + "\",\"parent_id\":" + (parentId == null ? "null" : "\"" + parentId + "\"") + "}");
        byte[] bytes = body.getBytes();
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream o = ex.getResponseBody()) { o.write(bytes); }
    }

    static String call(int port, String path, String traceparent) {
        try {
            return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path)).header("traceparent", traceparent).build(),
                    HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    static HttpServer server() throws IOException {
        HttpServer s = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        s.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        return s;
    }

    static void sleep(long ms) { try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); } }

    public static void main(String[] args) throws Exception {
        HttpServer pricing = server(), books = server(), gateway = server();
        pricing.createContext("/price", ex -> traced("pricing", ex, tp -> { sleep(120); return "12.99"; }));   // the slow hop (artificial)
        books.createContext("/books", ex -> traced("books", ex, tp -> {
            sleep(10);                                                                                        // "database"
            return "Dune, " + call(pricing.getAddress().getPort(), "/price/42", tp);
        }));
        gateway.createContext("/api", ex -> traced("gateway", ex, tp -> call(books.getAddress().getPort(), "/books/42", tp)));
        for (HttpServer s : List.of(pricing, books, gateway)) s.start();

        String reply = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + gateway.getAddress().getPort() + "/api/books/42")).build(),
                HttpResponse.BodyHandlers.ofString()).body();
        System.out.println("client got: " + reply + "\n");

        List<Span> all = new ArrayList<>(spans);
        all.sort(Comparator.comparingLong(Span::startMs));
        Span root = all.stream().filter(s -> s.parentId() == null).findFirst().orElseThrow();
        System.out.println("trace " + root.traceId() + "  (" + root.durMs() + " ms)");
        for (Span s : all) {
            int depth = 0;
            for (Span c = s; c.parentId() != null; depth++) { String p = c.parentId(); c = all.stream().filter(x -> x.spanId().equals(p)).findFirst().orElseThrow(); }
            int from = (int) ((s.startMs() - root.startMs()) * 30 / root.durMs());
            int width = Math.max(1, (int) (s.durMs() * 30 / root.durMs()));
            long self = s.durMs() - all.stream().filter(x -> s.spanId().equals(x.parentId())).mapToLong(Span::durMs).sum();
            System.out.printf("%-18s %s%s%s %4d ms (self %d, starts +%d)%n", "  ".repeat(depth) + s.service(), " ".repeat(from), "█".repeat(width),
                    " ".repeat(Math.max(0, 30 - from - width)), s.durMs(), self, s.startMs() - root.startMs());
        }
        for (HttpServer s : List.of(gateway, books, pricing)) s.stop(0);
    }
}
