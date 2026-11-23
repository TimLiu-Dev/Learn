import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

// Server-Sent Events: a server that streams "tokens", and a client that prints each line as it arrives.
// java Sse.java          -> flushes after every event
// java Sse.java noflush  -> forgets to flush
public class Sse {
    public static void main(String[] args) throws Exception {
        boolean flush = !(args.length > 0 && args[0].equals("noflush"));
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/stream", ex -> {
            ex.getResponseHeaders().set("Content-Type", "text/event-stream; charset=utf-8");
            ex.getResponseHeaders().set("Cache-Control", "no-cache");
            ex.sendResponseHeaders(200, 0);                  // 0 = length unknown: chunked encoding
            try (OutputStream out = ex.getResponseBody()) {
                write(out, ": stream open\n\n", flush);      // a comment: clients ignore it
                for (String token : new String[] {"Hel", "lo, ", "Ada"}) {
                    sleep(300);                              // pretend the model is thinking
                    write(out, "event: delta\ndata: " + token + "\n\n", flush);
                }
                write(out, "event: done\ndata: 3 tokens\n\n", flush);
            }                                                // closing ends the chunked body
        });
        server.start();
        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/stream");
            long start = System.nanoTime();
            HttpResponse<Stream<String>> resp = client.send(
                    HttpRequest.newBuilder(uri).header("Accept", "text/event-stream").build(),
                    HttpResponse.BodyHandlers.ofLines());    // returns before the body has finished
            System.out.println(resp.statusCode() + " " + resp.headers().firstValue("content-type").orElse("")
                    + " | transfer-encoding: " + resp.headers().firstValue("transfer-encoding").orElse("-"));
            try (Stream<String> lines = resp.body()) {
                lines.forEach(line -> System.out.printf("+%4d ms  %s%n",
                        (System.nanoTime() - start) / 1_000_000, line.isEmpty() ? "(blank line)" : line));
            }
        } finally {
            server.stop(0);
        }
    }

    static void write(OutputStream out, String s, boolean flush) throws java.io.IOException {
        out.write(s.getBytes(StandardCharsets.UTF_8));
        if (flush) out.flush();                              // push this chunk onto the wire now
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
