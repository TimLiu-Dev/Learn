import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Leader + one follower, two SQLite files. The follower copies the leader's change log `lagMs` late.
public class Replication {
    static class ReplicatedBooks implements AutoCloseable {
        private final Path dir;
        private final long lagMs;
        private final Connection leader, leaderLog, follower, followerRead;
        private volatile boolean running = true;

        ReplicatedBooks(Path dir, long lagMs) throws SQLException {
            this.dir = dir;
            this.lagMs = lagMs;
            leader = open("leader.db");         // used by writers
            leaderLog = open("leader.db");      // used by the replicator to read the change log
            follower = open("follower.db");     // used by the replicator to apply changes
            followerRead = open("follower.db"); // used by readers
            try (Statement st = leader.createStatement()) {
                st.executeUpdate("CREATE TABLE books(id INTEGER PRIMARY KEY, title TEXT NOT NULL)");
                st.executeUpdate("CREATE TABLE changes(version INTEGER PRIMARY KEY, at INTEGER, id INTEGER, title TEXT)");
            }
            try (Statement st = follower.createStatement()) {
                st.executeUpdate("CREATE TABLE books(id INTEGER PRIMARY KEY, title TEXT NOT NULL)");
                st.executeUpdate("CREATE TABLE applied(version INTEGER)");
                st.executeUpdate("INSERT INTO applied VALUES (0)");
            }
            Thread.ofVirtual().start(this::replicate);
        }

        private Connection open(String file) throws SQLException {
            return DriverManager.getConnection("jdbc:sqlite:" + dir.resolve(file) + "?busy_timeout=3000&journal_mode=WAL");
        }

        // A write goes to the leader: the row and its change-log entry in one transaction (L36).
        record Written(long id, long version) {}

        synchronized Written write(String title) throws SQLException {
            leader.setAutoCommit(false);
            try {
                long id;
                try (var st = leader.prepareStatement("INSERT INTO books(title) VALUES (?)")) {
                    st.setString(1, title);
                    st.executeUpdate();
                    id = lastRowId(leader);
                }
                try (var st = leader.prepareStatement("INSERT INTO changes(at, id, title) VALUES (?, ?, ?)")) {
                    st.setLong(1, System.currentTimeMillis()); st.setLong(2, id); st.setString(3, title);
                    st.executeUpdate();
                }
                long version = lastRowId(leader);
                leader.commit();
                return new Written(id, version);
            } finally {
                leader.setAutoCommit(true);
            }
        }

        String readLeader(long id) throws SQLException { return titleOf(leader, id); }
        String readFollower(long id) throws SQLException { return titleOf(followerRead, id); }
        long followerVersion() throws SQLException { return single(followerRead, "SELECT version FROM applied"); }
        synchronized long leaderVersion() throws SQLException { return single(leader, "SELECT coalesce(max(version), 0) FROM changes"); }

        private static long lastRowId(Connection c) throws SQLException { return single(c, "SELECT last_insert_rowid()"); }

        private static long single(Connection c, String sql) throws SQLException {
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) { return rs.getLong(1); }
        }

        private static String titleOf(Connection c, long id) throws SQLException {
            try (var st = c.prepareStatement("SELECT title FROM books WHERE id = ?")) {
                st.setLong(1, id);
                try (ResultSet rs = st.executeQuery()) { return rs.next() ? rs.getString(1) : null; }
            }
        }

        // The replication stream: apply every change, in order, once it is `lagMs` old.
        private void replicate() {
            try {
                while (running) {
                    try (var st = leaderLog.prepareStatement("SELECT version, at, id, title FROM changes WHERE version > ? ORDER BY version")) {
                        st.setLong(1, followerVersion());
                        try (ResultSet rs = st.executeQuery()) {
                            while (rs.next()) {
                                long wait = rs.getLong("at") + lagMs - System.currentTimeMillis();
                                if (wait > 0) Thread.sleep(wait);               // the artificial network delay
                                follower.setAutoCommit(false);
                                try (var ins = follower.prepareStatement("INSERT OR REPLACE INTO books(id, title) VALUES (?, ?)");
                                     var upd = follower.prepareStatement("UPDATE applied SET version = ?")) {
                                    ins.setLong(1, rs.getLong("id")); ins.setString(2, rs.getString("title")); ins.executeUpdate();
                                    upd.setLong(1, rs.getLong("version")); upd.executeUpdate();
                                }
                                follower.commit();
                                follower.setAutoCommit(true);
                            }
                        }
                    }
                    Thread.sleep(10);
                }
            } catch (SQLException | InterruptedException e) {
                if (running) e.printStackTrace();
            }
        }

        @Override public void close() throws Exception {
            running = false;
            Thread.sleep(50);
            for (Connection c : new Connection[] { leader, leaderLog, follower, followerRead }) c.close();
        }
    }

    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("l42");
        try (var db = new ReplicatedBooks(dir, 300)) {
            System.out.println("-- read from the follower right after a write");
            long v1 = db.write("Dune").version();
            System.out.println("write 'Dune'           -> leader version " + v1);
            String now = db.readFollower(1);
            System.out.println("read book 1, follower  -> " + (now == null ? "not found" : now) + "   (follower at version " + db.followerVersion() + ")");
            Thread.sleep(400);
            System.out.println("400 ms later, follower -> " + db.readFollower(1) + "   (follower at version " + db.followerVersion() + ")");

            System.out.println("-- read-your-writes: remember your write's version, read where it is visible");
            long mine = db.write("Emma").version();
            System.out.println("write 'Emma' -> version " + mine + "; read now -> " + readMine(db, 2, mine));
            Thread.sleep(400);
            System.out.println("400 ms later           -> " + readMine(db, 2, mine));

            System.out.println("-- lag under a burst of 20 writes");
            for (int i = 0; i < 20; i++) db.write("book " + i);
            long t0 = System.currentTimeMillis();
            while (true) {
                long lv = db.leaderVersion(), fv = db.followerVersion();
                System.out.printf("  +%3d ms  leader v%d  follower v%d  behind by %d%n", System.currentTimeMillis() - t0, lv, fv, lv - fv);
                if (fv == lv) break;
                Thread.sleep(100);
            }
        }
        try (var files = Files.walk(dir)) {
            files.sorted(java.util.Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }

    static String readMine(ReplicatedBooks db, long id, long minVersion) throws SQLException {
        long fv = db.followerVersion();
        return fv >= minVersion ? db.readFollower(id) + " from follower (at " + fv + ")"
                                : db.readLeader(id) + " from leader (follower only at " + fv + ", need " + minVersion + ")";
    }
}
