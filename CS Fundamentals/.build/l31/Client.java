import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// Run TinyServer in serve mode first: java TinyServer.java serve 60
public class Client {
    static final String BASE = "http://127.0.0.1:18131";

    static HttpRequest get(String path) {
        return HttpRequest.newBuilder(URI.create(BASE + path)).build();   // GET is the default
    }

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();                                  // redirects: NEVER by default

        // 1. synchronous GET: send() blocks until the whole response has arrived
        HttpResponse<String> res = client.send(get("/hello?name=Ada"), BodyHandlers.ofString());
        System.out.println("1 GET     : " + res.statusCode() + " " + res.version() + " "
                + res.headers().firstValue("content-type").orElse("?") + " | " + res.body().strip());

        // 2. redirects: the default client hands you the 301; NORMAL follows it
        HttpResponse<Void> moved = client.send(get("/old"), BodyHandlers.discarding());
        System.out.println("2 NEVER   : " + moved.statusCode() + " Location: "
                + moved.headers().firstValue("location").orElse("?"));
        HttpClient follower = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpResponse<String> landed = follower.send(get("/old"), BodyHandlers.ofString());
        System.out.println("2 NORMAL  : " + landed.statusCode() + " from " + landed.uri() + " | " + landed.body().strip());

        // 3. POST with a body
        HttpRequest post = HttpRequest.newBuilder(URI.create(BASE + "/echo"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"x\":1}"))
                .build();
        System.out.println("3 POST    : " + client.send(post, BodyHandlers.ofString()).body().strip());

        // 4. asynchronous: three requests in flight at once (L28's CompletableFuture)
        List<CompletableFuture<String>> futures = List.of("/", "/hello?name=Bo", "/nope").stream()
                .map(p -> client.sendAsync(get(p), BodyHandlers.ofString())
                        .thenApply(r -> p + " -> " + r.statusCode()))
                .toList();
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        System.out.println("4 async   : " + futures.stream().map(CompletableFuture::join).toList());

        // 5. a per-request timeout
        HttpRequest slow = HttpRequest.newBuilder(URI.create(BASE + "/slow"))
                .timeout(Duration.ofMillis(500)).build();
        long t0 = System.nanoTime();
        try {
            client.send(slow, BodyHandlers.ofString());
        } catch (HttpTimeoutException e) {
            System.out.printf("5 timeout : %s: %s after %d ms%n", e.getClass().getSimpleName(),
                    e.getMessage(), (System.nanoTime() - t0) / 1_000_000);
        }

        // 6. HTTPS to a real site: the same client negotiates HTTP/2
        HttpRequest head = HttpRequest.newBuilder(URI.create("https://example.com/")).HEAD().build();
        HttpResponse<Void> remote = client.send(head, BodyHandlers.discarding());
        System.out.println("6 HTTPS   : " + remote.statusCode() + " " + remote.version());
    }
}
