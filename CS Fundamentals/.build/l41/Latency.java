import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executors;

// Measure four latencies on this machine: a HashMap get, an indexed SQLite
// lookup, a localhost HTTP round trip, and a cold vs warm method call.
public class Latency {
    public static void main(String[] args) throws Exception {
        // 1. HashMap get: 100,000 entries, 10 million random lookups
        var rnd = new Random(41);
        var map = new HashMap<Integer, String>();
        for (int i = 0; i < 100_000; i++) map.put(i, "book-" + i);
        int[] keys = new int[10_000_000];
        for (int i = 0; i < keys.length; i++) keys[i] = rnd.nextInt(100_000);
        long sink = 0;
        for (int k : keys) sink += map.get(k).length();          // warm-up pass
        long t = System.nanoTime();
        for (int k : keys) sink += map.get(k).length();
        double mapNs = (System.nanoTime() - t) / (double) keys.length;

        // 2. SQLite: 100,000 rows, lookup by an indexed column
        Path db = Files.createTempFile("l41-latency", ".db");
        double sqlUs;
        try (var c = DriverManager.getConnection("jdbc:sqlite:" + db)) {
            try (var s = c.createStatement()) {
                s.execute("CREATE TABLE books(id INTEGER PRIMARY KEY, isbn TEXT, title TEXT)");
                s.execute("CREATE INDEX books_isbn ON books(isbn)");
            }
            c.setAutoCommit(false);
            try (var ins = c.prepareStatement("INSERT INTO books(isbn, title) VALUES (?, ?)")) {
                for (int i = 0; i < 100_000; i++) {
                    ins.setString(1, "978" + (1_000_000 + i)); ins.setString(2, "Title " + i); ins.addBatch();
                }
                ins.executeBatch();
            }
            c.commit();
            try (var q = c.prepareStatement("SELECT title FROM books WHERE isbn = ?")) {
                for (int i = 0; i < 2_000; i++) sink += lookup(q, rnd);    // warm-up
                t = System.nanoTime();
                for (int i = 0; i < 20_000; i++) sink += lookup(q, rnd);
                sqlUs = (System.nanoTime() - t) / 20_000.0 / 1_000;
            }
        }
        Files.delete(db);

        // 3. HTTP round trip to a server on 127.0.0.1
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/ping", ex -> {
            byte[] body = "ok".getBytes();
            ex.sendResponseHeaders(200, body.length);
            try (var out = ex.getResponseBody()) { out.write(body); }
        });
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        var client = HttpClient.newHttpClient();
        var req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/ping")).build();
        for (int i = 0; i < 500; i++) client.send(req, HttpResponse.BodyHandlers.ofString());   // warm-up
        t = System.nanoTime();
        for (int i = 0; i < 5_000; i++) sink += client.send(req, HttpResponse.BodyHandlers.ofString()).body().length();
        double httpUs = (System.nanoTime() - t) / 5_000.0 / 1_000;
        server.stop(0);

        // 4. Cold vs warm: the first call loads and links classes; later calls are JIT-compiled
        t = System.nanoTime();
        sink += String.format("%08.3f|%s", 3.14159, List.of(1, 2)).length();
        double coldUs = (System.nanoTime() - t) / 1_000.0;
        for (int i = 0; i < 100_000; i++) sink += String.format("%08.3f|%s", 3.14159, List.of(1, 2)).length();
        t = System.nanoTime();
        for (int i = 0; i < 10_000; i++) sink += String.format("%08.3f|%s", 3.14159, List.of(1, 2)).length();
        double warmUs = (System.nanoTime() - t) / 10_000.0 / 1_000;

        System.out.printf("HashMap get            %10.1f ns%n", mapNs);
        System.out.printf("SQLite indexed lookup  %10.1f µs%n", sqlUs);
        System.out.printf("HTTP round trip (lo0)  %10.1f µs%n", httpUs);
        System.out.printf("String.format, cold    %10.1f µs%n", coldUs);
        System.out.printf("String.format, warm    %10.1f µs%n", warmUs);
        System.out.println("(sink " + (sink % 10) + ")");
    }

    static int lookup(PreparedStatement q, Random rnd) throws SQLException {
        q.setString(1, "978" + (1_000_000 + rnd.nextInt(100_000)));
        try (var rs = q.executeQuery()) { return rs.next() ? rs.getString(1).length() : 0; }
    }
}
