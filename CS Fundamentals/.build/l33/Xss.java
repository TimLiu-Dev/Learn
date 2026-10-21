import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Xss {
    // The five characters OWASP says to encode in HTML body text
    static String escapeHtml(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#x27;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    static String nameParam(HttpExchange ex) {
        String q = ex.getRequestURI().getRawQuery();
        return q == null ? "" : URLDecoder.decode(q.replaceFirst("^name=", ""), StandardCharsets.UTF_8);
    }

    static void send(HttpExchange ex, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = ex.getResponseBody()) { out.write(bytes); }
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);  // port 0: any free port
        server.createContext("/unsafe", ex -> send(ex, "<p>Thanks for signing, " + nameParam(ex) + "!</p>"));
        server.createContext("/safe", ex -> {
            ex.getResponseHeaders().set("Content-Security-Policy", "default-src 'self'");
            send(ex, "<p>Thanks for signing, " + escapeHtml(nameParam(ex)) + "!</p>");
        });
        server.start();
        try {
            HttpClient client = HttpClient.newHttpClient();
            String input = URLEncoder.encode("<script>alert('hi')</script>", StandardCharsets.UTF_8);
            for (String path : new String[]{"unsafe", "safe"}) {
                URI uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/" + path + "?name=" + input);
                HttpResponse<String> res = client.send(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString());
                System.out.println("/" + path + " -> " + res.body());
            }
        } finally {
            server.stop(0);
        }
    }
}
