import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.concurrent.Executors;

// An origin server that speaks HTTP caching. java EtagServer.java 60 -> serves on :18132 for 60 s
public class EtagServer {
    static final String REPORT = "Q3 report: revenue up 4%, costs flat.\n";

    static String etagOf(String body) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(body.getBytes(StandardCharsets.UTF_8));
        return "\"" + HexFormat.of().formatHex(hash).substring(0, 16) + "\"";   // quotes are part of an ETag
    }

    static void send(HttpExchange ex, int status, String cacheControl, String etag, String body) throws IOException {
        if (cacheControl != null) ex.getResponseHeaders().set("Cache-Control", cacheControl);
        if (etag != null) ex.getResponseHeaders().set("ETag", etag);
        ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        boolean noBody = status == 304 || ex.getRequestMethod().equals("HEAD");
        ex.sendResponseHeaders(status, noBody ? -1 : bytes.length);              // -1 = no body
        try (var out = ex.getResponseBody()) { if (!noBody) out.write(bytes); }
        System.out.println("  [origin] " + ex.getRequestMethod() + " " + ex.getRequestURI() + " -> " + status);
    }

    public static void main(String[] args) throws Exception {
        int seconds = args.length > 0 ? Integer.parseInt(args[0]) : 30;
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 18132), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.createContext("/report", ex -> {
            try {
                String etag = etagOf(REPORT);
                String ifNoneMatch = ex.getRequestHeaders().getFirst("If-None-Match");
                if (etag.equals(ifNoneMatch)) send(ex, 304, "no-cache", etag, "");   // you already have it
                else send(ex, 200, "no-cache", etag, REPORT);
            } catch (Exception e) { throw new IOException(e); }
        });
        server.createContext("/time", ex ->
                send(ex, 200, "max-age=2", null, LocalTime.now().truncatedTo(ChronoUnit.MILLIS) + "\n"));
        server.createContext("/secret", ex -> send(ex, 200, "no-store", null, "balance: 42\n"));
        server.start();
        System.out.println("origin on http://127.0.0.1:18132 for " + seconds + " s");
        Thread.sleep(seconds * 1000L);
        server.stop(0);
    }
}
