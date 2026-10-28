import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

// Write-behind: writes land in the cache at once and reach the database later,
// in batches. Run "write" (which crashes on purpose), then "check".
public class WriteBehind {
    static final Path DB = Path.of(System.getProperty("java.io.tmpdir"), "l41-writebehind.db");

    public static void main(String[] args) throws Exception {
        if (args[0].equals("check")) { check(); return; }
        Files.deleteIfExists(DB);
        try (var c = DriverManager.getConnection("jdbc:sqlite:" + DB); var s = c.createStatement()) {
            s.execute("CREATE TABLE stock(sku TEXT PRIMARY KEY, qty INTEGER NOT NULL)");
            s.execute("INSERT INTO stock VALUES ('dune', 10), ('emma', 10), ('ulysses', 10)");
        }
        var cache = new ConcurrentHashMap<String, Integer>(Map.of("dune", 10, "emma", 10, "ulysses", 10));
        var dirty = new LinkedBlockingQueue<String>();       // keys waiting to be written

        // The flusher: every 500 ms, write all dirty keys to the database in one transaction
        var flusher = Executors.newSingleThreadScheduledExecutor();
        flusher.scheduleAtFixedRate(() -> {
            var batch = new ArrayList<String>();
            dirty.drainTo(batch);
            if (batch.isEmpty()) return;
            try (var c = DriverManager.getConnection("jdbc:sqlite:" + DB);
                 var u = c.prepareStatement("UPDATE stock SET qty = ? WHERE sku = ?")) {
                c.setAutoCommit(false);
                for (String sku : new LinkedHashSet<>(batch)) { u.setInt(1, cache.get(sku)); u.setString(2, sku); u.executeUpdate(); }
                c.commit();
                System.out.println("  flushed " + batch.size() + " writes");
            } catch (SQLException e) { throw new IllegalStateException(e); }
        }, 500, 500, TimeUnit.MILLISECONDS);

        // Writes are instant: cache first, database "later"
        sell(cache, dirty, "dune", 3); sell(cache, dirty, "emma", 1);
        Thread.sleep(700);                                      // one flush happens
        sell(cache, dirty, "dune", 2); sell(cache, dirty, "ulysses", 4); sell(cache, dirty, "emma", 5);
        System.out.println("cache says: " + new TreeMap<>(cache) + ", queued: " + dirty.size());
        System.out.println("power cut!");
        Runtime.getRuntime().halt(1);                           // no shutdown hooks, no final flush
    }

    static void sell(Map<String, Integer> cache, BlockingQueue<String> dirty, String sku, int n) {
        cache.merge(sku, -n, Integer::sum);
        dirty.add(sku);
    }

    static void check() throws SQLException {
        var rows = new TreeMap<String, Integer>();
        try (var c = DriverManager.getConnection("jdbc:sqlite:" + DB);
             var rs = c.createStatement().executeQuery("SELECT sku, qty FROM stock")) {
            while (rs.next()) rows.put(rs.getString(1), rs.getInt(2));
        }
        System.out.println("database says: " + rows);
    }
}
