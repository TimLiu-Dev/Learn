import com.sun.net.httpserver.HttpServer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FakeHttp {
    // The service's view of the outside world: one small interface it owns.
    @FunctionalInterface
    interface HttpPoster {
        int post(String url, String body);              // returns the status code
    }

    static final class FeeNotifier {
        private final HttpPoster http;
        private final String baseUrl;
        FeeNotifier(HttpPoster http, String baseUrl) { this.http = http; this.baseUrl = baseUrl; }
        boolean lateFee(String member, int cents) {
            int status = http.post(baseUrl + "/payments", "member=" + member + "&cents=" + cents);
            return status >= 200 && status <= 299;
        }
    }

    // Real: wraps the JDK's HttpClient (L31).
    static final class JdkHttpPoster implements HttpPoster {
        private final HttpClient client = HttpClient.newHttpClient();
        public int post(String url, String body) {
            try {
                return client.send(HttpRequest.newBuilder(URI.create(url)).POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                        HttpResponse.BodyHandlers.discarding()).statusCode();
            } catch (Exception e) { throw new IllegalStateException(e); }
        }
    }

    // Fake: records every call, answers with a status we choose. No network at all.
    static final class RecordingHttpPoster implements HttpPoster {
        final List<String> calls = new ArrayList<>();
        private final int status;
        RecordingHttpPoster(int status) { this.status = status; }
        public int post(String url, String body) { calls.add("POST " + url + " " + body); return status; }
    }

    public static void main(String[] args) throws Exception {
        // A throwaway payments server on localhost, so the real poster has something to talk to.
        List<String> received = new CopyOnWriteArrayList<>();
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/payments", ex -> {
            received.add(new String(ex.getRequestBody().readAllBytes()));
            ex.sendResponseHeaders(201, -1);
            ex.close();
        });
        server.start();
        try {
            String base = "http://127.0.0.1:" + server.getAddress().getPort();
            long t = System.nanoTime();
            boolean real = new FeeNotifier(new JdkHttpPoster(), base).lateFee("ada", 75);
            System.out.println("real:  ok=" + real + " server got=" + received + "  (" + (System.nanoTime() - t) / 1_000_000 + " ms)");

            RecordingHttpPoster fake = new RecordingHttpPoster(201);
            t = System.nanoTime();
            boolean ok = new FeeNotifier(fake, "https://payments.example").lateFee("ada", 75);
            System.out.println("fake:  ok=" + ok + " calls=" + fake.calls + "  (" + (System.nanoTime() - t) / 1_000 + " µs)");
            System.out.println("fake 503: ok=" + new FeeNotifier(new RecordingHttpPoster(503), "https://payments.example").lateFee("ada", 75));
        } finally {
            server.stop(0);
        }
    }
}
