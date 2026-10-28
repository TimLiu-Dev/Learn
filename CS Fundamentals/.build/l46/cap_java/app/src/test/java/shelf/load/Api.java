package shelf.load;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/** A tiny HTTP client for the demos. */
public final class Api {
    private final String base;
    private final HttpClient client = HttpClient.newBuilder().executor(Executors.newVirtualThreadPerTaskExecutor()).build();

    public Api(int port) {
        this.base = "http://127.0.0.1:" + port;
    }

    public HttpResponse<String> get(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(base + path)).build(), HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> post(String path, String json) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(base + path)).header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json)).build(), HttpResponse.BodyHandlers.ofString());
    }

    public long newBook(String title) throws Exception {
        return id(post("/books", "{\"title\":\"" + title + "\",\"author\":\"Frank Herbert\"}"));
    }

    @FunctionalInterface
    public interface Call {
        HttpResponse<String> run(int i) throws Exception;
    }

    /** n virtual threads wait at a start line, then all call together. */
    public static List<Integer> burst(int n, Call call) throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Integer>> futures = IntStream.range(0, n)
                .mapToObj(i -> pool.submit(() -> { start.await(); return call.run(i).statusCode(); })).toList();
            start.countDown();
            List<Integer> codes = new java.util.ArrayList<>();
            for (Future<Integer> f : futures) codes.add(f.get());
            return codes;
        }
    }

    private static final Pattern ID = Pattern.compile("\"id\":(\\d+)");

    public static long id(HttpResponse<String> r) {
        Matcher m = ID.matcher(r.body());
        if (!m.find()) throw new IllegalStateException(r.body());
        return Long.parseLong(m.group(1));
    }

    public static String tally(List<Integer> codes) {
        Map<Integer, Integer> counts = new TreeMap<>();
        codes.forEach(c -> counts.merge(c, 1, Integer::sum));
        StringBuilder sb = new StringBuilder();
        counts.forEach((code, n) -> sb.append(sb.isEmpty() ? "" : "  ").append(code).append("×").append(n));
        return sb.toString();
    }
}
