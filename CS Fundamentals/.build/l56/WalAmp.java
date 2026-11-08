import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

// How many bytes does a B-tree database write per small row? Count what lands in SQLite's write-ahead log.
public class WalAmp {
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("l56");
        int rows = 2_000;
        var runs = new LinkedHashMap<String, Integer>();
        runs.put("1 row per commit", 1); runs.put("100 rows per commit", 100); runs.put("all rows in 1 commit", rows);
        for (var run : runs.entrySet()) {
            int perCommit = run.getValue();
            Path db = dir.resolve(perCommit + ".db");
            try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + db)) {
                try (Statement s = c.createStatement()) {
                    s.execute("PRAGMA journal_mode = WAL");
                    s.execute("PRAGMA wal_autocheckpoint = 0");      // keep every frame in the WAL so we can measure it
                    s.execute("CREATE TABLE kv (k TEXT PRIMARY KEY, v TEXT) WITHOUT ROWID");   // one B-tree
                }
                c.setAutoCommit(false);
                Random rnd = new Random(56);
                long logBytes = 0;                                   // what Part 1's log would have written
                try (PreparedStatement ins = c.prepareStatement("INSERT OR REPLACE INTO kv VALUES (?, ?)")) {
                    for (int i = 1; i <= rows; i++) {
                        String k = String.format("user%05d", rnd.nextInt(50_000)), v = "v" + i;
                        ins.setString(1, k); ins.setString(2, v); ins.executeUpdate();
                        logBytes += LogKV.encode(k, v).remaining();
                        if (i % perCommit == 0) c.commit();
                    }
                }
                long wal = Files.size(dir.resolve(perCommit + ".db-wal"));
                System.out.println(String.format("%-21s WAL %,10d bytes = %,6d per row, %6.1f× a plain log",
                        run.getKey(), wal, wal / rows, (double) wal / logBytes));
            }
        }
        try (Stream<Path> s = Files.walk(dir)) { s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
    }
}
