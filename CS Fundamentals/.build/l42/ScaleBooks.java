import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

// Run with: java -cp sqlite-jdbc.jar ScaleBooks.java  (next to LoadBalancer.java, JobQueue.java,
// Replication.java and Backend.java: the source launcher finds those classes by file name)
public class ScaleBooks {
    static String signToken(String user, byte[] key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(user.getBytes())).substring(0, 16);
        } catch (Exception e) { throw new IllegalStateException(e); }
    }

    // A stateless books backend: everything it knows lives in the database, the queue or the token.
    static class BooksBackend {
        final String name;
        final HttpServer server;
        private final Replication.ReplicatedBooks db;
        private final JobQueue jobs;
        private final byte[] key;

        BooksBackend(String name, Replication.ReplicatedBooks db, JobQueue jobs, byte[] key) throws IOException {
            this.name = name; this.db = db; this.jobs = jobs; this.key = key;
            server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 100);
            server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
            server.createContext("/health", ex -> Backend.reply(ex, 200, "ok"));   // without this, the LB marks us DOWN
            server.createContext("/login", ex -> {
                String user = ex.getRequestURI().getQuery().replaceFirst("^user=", "");
                Backend.reply(ex, 200, user + "." + signToken(user, key));
            });
            server.createContext("/books", ex -> {
                try {
                    if (ex.getRequestMethod().equals("POST")) create(ex); else read(ex);
                } catch (SQLException | InterruptedException e) {
                    Backend.reply(ex, 500, "server error");
                }
            });
            server.start();
        }

        URI uri() { return URI.create("http://127.0.0.1:" + server.getAddress().getPort()); }

        private String user(HttpExchange ex) {
            String token = ex.getRequestHeaders().getFirst("Token");
            String[] parts = (token == null ? "." : token).split("\\.", 2);
            boolean ok = parts.length == 2 && MessageDigest.isEqual(parts[1].getBytes(), signToken(parts[0], key).getBytes());
            return ok ? parts[0] : null;
        }

        private void create(HttpExchange ex) throws IOException, SQLException, InterruptedException {
            if (user(ex) == null) { Backend.reply(ex, 401, "bad token"); return; }
            var written = db.write(new String(ex.getRequestBody().readAllBytes()));   // to the leader
            jobs.push("thumbnail " + written.id());                                   // slow work: later
            ex.getResponseHeaders().add("X-Version", Long.toString(written.version()));
            Backend.reply(ex, 201, Long.toString(written.id()));
        }

        private void read(HttpExchange ex) throws IOException, SQLException {
            String path = ex.getRequestURI().getPath();
            long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
            String header = ex.getRequestHeaders().getFirst("X-Min-Version");
            long needed = header == null ? 0 : Long.parseLong(header);
            boolean fromReplica = db.followerVersion() >= needed;                       // read-your-writes
            String title = fromReplica ? db.readFollower(id) : db.readLeader(id);
            ex.getResponseHeaders().add("X-Served-By", name + "/" + (fromReplica ? "replica" : "leader"));
            if (title == null) Backend.reply(ex, 404, "no book " + id); else Backend.reply(ex, 200, title);
        }
    }

    static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    static HttpResponse<String> send(HttpRequest.Builder b) throws Exception {
        return client.send(b.build(), HttpResponse.BodyHandlers.ofString());
    }

    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("l42final");
        byte[] key = "shared-secret-from-config".getBytes();
        var db = new Replication.ReplicatedBooks(dir, 300);
        var jobs = new JobQueue(100, 300, 3);
        List<BooksBackend> backends = List.of(new BooksBackend("b1", db, jobs, key),
            new BooksBackend("b2", db, jobs, key), new BooksBackend("b3", db, jobs, key));
        List<LoadBalancer.Target> targets = backends.stream().map(b -> new LoadBalancer.Target(b.name, b.uri())).toList();
        var lb = new LoadBalancer(targets, LoadBalancer.Strategy.ROUND_ROBIN, true);

        // Two thumbnail workers. Idempotent: a job whose thumbnail exists is acked and skipped.
        Set<String> thumbnails = ConcurrentHashMap.newKeySet();
        var made = new AtomicInteger();
        var skipped = new AtomicInteger();
        var crashOnce = new AtomicBoolean(true);
        List<Thread> workers = IntStream.rangeClosed(1, 2).mapToObj(w -> Thread.ofVirtual().start(() -> {
            try {
                while (true) {
                    var m = jobs.receive(800);
                    if (m == null) break;
                    if (thumbnails.contains(m.body())) { skipped.incrementAndGet(); jobs.ack(m.id()); continue; }
                    Thread.sleep(30);                                  // "resize the cover image"
                    thumbnails.add(m.body());
                    made.incrementAndGet();
                    if (w == 1 && crashOnce.getAndSet(false)) { lb.log("worker 1 made '" + m.body() + "', then CRASHED before ack"); continue; }
                    jobs.ack(m.id());
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        })).toList();

        String token = send(HttpRequest.newBuilder(lb.uri().resolve("/login?user=ada"))).body();
        int errors = 0, leaderReads = 0, staleReads = 0;
        for (int i = 0; i < 20; i++) {
            if (i == 10) { backends.get(1).server.stop(0); lb.log("b2 crashed"); }
            var post = send(HttpRequest.newBuilder(lb.uri().resolve("/books")).header("Token", token)
                .POST(HttpRequest.BodyPublishers.ofString("Book " + i)));
            if (post.statusCode() != 201) { errors++; lb.log("POST Book " + i + " -> " + post.statusCode() + " " + post.body()); continue; }
            String id = post.body();
            String version = post.headers().firstValue("X-Version").orElseThrow();
            // the writer reads its own write: it sends the version it saw
            var mine = send(HttpRequest.newBuilder(lb.uri().resolve("/books/" + id)).header("X-Min-Version", version));
            if (mine.statusCode() != 200) errors++;
            if (mine.headers().firstValue("X-Served-By").orElse("").endsWith("leader")) leaderReads++;
            // someone else reads the same book straight away, without a version
            var other = send(HttpRequest.newBuilder(lb.uri().resolve("/books/" + id)));
            if (other.statusCode() == 404) staleReads++;
            Thread.sleep(50);
        }
        for (Thread t : workers) t.join();

        System.out.println("per-backend requests: " + targets.stream().map(Object::toString).collect(Collectors.joining(" ")));
        System.out.println("errors seen by the client: " + errors);
        System.out.println("own reads routed to the leader (replica behind): " + leaderReads + " of 20");
        System.out.println("other client's reads that got 404 (replica behind): " + staleReads + " of 20");
        System.out.println("thumbnail jobs: " + thumbnails.size() + " distinct, made " + made.get() + " times, duplicates skipped: " + skipped.get());

        lb.close(); jobs.close(); db.close();
        backends.forEach(b -> b.server.stop(0));
        try (var files = Files.walk(dir)) { files.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
    }
}
