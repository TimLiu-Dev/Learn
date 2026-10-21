import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// Exercise 3: validate with Last-Modified / If-Modified-Since instead of an ETag.
public class Ex3 {
    static final DateTimeFormatter HTTP_DATE = DateTimeFormatter.RFC_1123_DATE_TIME;   // "Tue, 20 Oct 2026 09:00:00 GMT"
    static final ZonedDateTime LAST_MODIFIED = ZonedDateTime.parse("Tue, 20 Oct 2026 09:00:00 GMT", HTTP_DATE);

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/notes", ex -> {
            String ims = ex.getRequestHeaders().getFirst("If-Modified-Since");
            ex.getResponseHeaders().set("Last-Modified", HTTP_DATE.format(LAST_MODIFIED));
            ex.getResponseHeaders().set("Cache-Control", "no-cache");
            boolean unchanged = ims != null && !LAST_MODIFIED.isAfter(ZonedDateTime.parse(ims, HTTP_DATE));
            if (unchanged) { ex.sendResponseHeaders(304, -1); ex.close(); return; }
            byte[] body = "meeting notes, v3".getBytes();
            ex.sendResponseHeaders(200, body.length);
            try (var out = ex.getResponseBody()) { out.write(body); }
        });
        server.start();
        try {
            var client = HttpClient.newHttpClient();
            var url = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/notes");
            var first = client.send(HttpRequest.newBuilder(url).build(), HttpResponse.BodyHandlers.ofString());
            String lm = first.headers().firstValue("Last-Modified").orElseThrow();
            System.out.println("1st: " + first.statusCode() + "  Last-Modified: " + lm + "  body: " + first.body());
            for (String since : new String[] {lm, "Mon, 19 Oct 2026 09:00:00 GMT"}) {
                var r = client.send(HttpRequest.newBuilder(url).header("If-Modified-Since", since).build(),
                        HttpResponse.BodyHandlers.ofString());
                System.out.println("If-Modified-Since " + since + " -> " + r.statusCode()
                        + (r.body().isEmpty() ? "  (no body)" : "  body: " + r.body()));
            }
        } finally {
            server.stop(0);
        }
    }
}
