import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

// An SSE feed that drops the connection every 2 events, and a client that resumes with Last-Event-ID.
public class Ex4 {
    static final List<String> MESSAGES = List.of("order placed", "payment ok", "packed", "shipped", "delivered");

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/feed", ex -> {
            String last = ex.getRequestHeaders().getFirst("Last-Event-ID");
            int from = last == null ? 0 : Integer.parseInt(last);            // ids are 1-based
            ex.getResponseHeaders().set("Content-Type", "text/event-stream; charset=utf-8");
            ex.sendResponseHeaders(200, 0);
            try (OutputStream out = ex.getResponseBody()) {
                int sent = 0;
                for (int id = from + 1; id <= MESSAGES.size() && sent < 2; id++, sent++) {
                    out.write(("id: " + id + "\ndata: " + MESSAGES.get(id - 1) + "\n\n").getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }
                if (from + sent == MESSAGES.size()) out.write("event: done\ndata: end\n\n".getBytes(StandardCharsets.UTF_8));
            }                                                                // the "drop": just close
        });
        server.start();
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/feed");
            String lastId = null;
            boolean done = false;
            for (int attempt = 1; !done && attempt <= 10; attempt++) {
                System.out.println("connect #" + attempt + " (Last-Event-ID: " + (lastId == null ? "none" : lastId) + ")");
                HttpRequest.Builder req = HttpRequest.newBuilder(uri).header("Accept", "text/event-stream");
                if (lastId != null) req.header("Last-Event-ID", lastId);
                HttpResponse<Stream<String>> resp = client.send(req.build(), HttpResponse.BodyHandlers.ofLines());
                String id = null, event = "message", data = null;
                try (Stream<String> body = resp.body()) {
                    for (String line : (Iterable<String>) body::iterator) {
                        if (line.startsWith("id:")) id = line.substring(3).trim();
                        else if (line.startsWith("event:")) event = line.substring(6).trim();
                        else if (line.startsWith("data:")) data = line.substring(5).trim();
                        else if (line.isEmpty()) {                           // a blank line ends the event
                            if (id != null) lastId = id;
                            System.out.println("  " + event + (id == null ? "" : " #" + id) + ": " + data);
                            if (event.equals("done")) done = true;
                            id = null; event = "message"; data = null;
                        }
                    }
                }                                                            // stream ended: reconnect
            }
        } finally {
            server.stop(0);
        }
    }
}
