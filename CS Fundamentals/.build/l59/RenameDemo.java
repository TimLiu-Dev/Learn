import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RenameDemo {
    // Three releases of the same "members" code. Each one runs with its own connection,
    // the way two servers would share one database.
    interface Release {
        String name();
        void add(Connection c, String fullName, String email) throws SQLException;
        String nameOf(Connection c, long id) throws SQLException;
    }

    static final Release V1 = new Release() {          // knows only the old column
        public String name() { return "v1"; }
        public void add(Connection c, String fullName, String email) throws SQLException {
            update(c, "INSERT INTO members (name, email) VALUES (?, ?)", fullName, email);
        }
        public String nameOf(Connection c, long id) throws SQLException {
            return one(c, "SELECT name FROM members WHERE id = ?", id);
        }
    };

    static final Release V2 = new Release() {          // writes both, reads the new one with a fallback
        public String name() { return "v2"; }
        public void add(Connection c, String fullName, String email) throws SQLException {
            update(c, "INSERT INTO members (name, full_name, email) VALUES (?, ?, ?)", fullName, fullName, email);
        }
        public String nameOf(Connection c, long id) throws SQLException {
            return one(c, "SELECT COALESCE(full_name, name) FROM members WHERE id = ?", id);
        }
    };

    static final Release V3 = new Release() {          // knows only the new column
        public String name() { return "v3"; }
        public void add(Connection c, String fullName, String email) throws SQLException {
            update(c, "INSERT INTO members (full_name, email) VALUES (?, ?)", fullName, email);
        }
        public String nameOf(Connection c, long id) throws SQLException {
            return one(c, "SELECT full_name FROM members WHERE id = ?", id);
        }
    };

    record Traffic(String live, int failed, int wrong) {
        @Override public String toString() { return "%-8s %3d   %3d".formatted(live, failed, wrong); }
    }

    static class World {
        final Path dbFile;
        final Map<Long, String> expected = new LinkedHashMap<>();   // what every read should return
        int next = 0;

        World(Path dbFile) { this.dbFile = dbFile; }

        Connection open() throws SQLException { return DriverManager.getConnection("jdbc:sqlite:" + dbFile); }

        /** 20 requests spread over the live releases: each adds a member, then reads every member. */
        Traffic traffic(List<Release> live) throws SQLException {
            int failed = 0, wrong = 0;
            Map<Release, Connection> conns = new HashMap<>();
            for (Release r : live) conns.put(r, open());
            for (int i = 0; i < 20; i++) {
                Release r = live.get(i % live.size());
                Connection c = conns.get(r);
                try {
                    String n = "Member " + (++next);
                    r.add(c, n, "m" + next + "@example.com");
                    expected.put(lastId(c), n);
                    for (var e : expected.entrySet()) {
                        if (!e.getValue().equals(r.nameOf(c, e.getKey()))) wrong++;
                    }
                } catch (SQLException e) {
                    failed++;
                }
            }
            for (Connection c : conns.values()) c.close();
            return new Traffic(live.stream().map(Release::name).collect(Collectors.joining("+")), failed, wrong);
        }

        List<Migrator.Migration> migrateTo(int version, Path dir) throws Exception {
            try (Connection c = open()) {
                return Migrator.migrate(c, Migrator.load(dir).stream().filter(m -> m.version() <= version).toList());
            }
        }

        /** Copies name into full_name a few rows per transaction, so no lock is held for long. */
        List<Integer> backfill(int batch) throws SQLException {
            List<Integer> sizes = new ArrayList<>();
            try (Connection c = open()) {
                while (true) {
                    int n = update(c, """
                        UPDATE members SET full_name = name WHERE id IN
                          (SELECT id FROM members WHERE full_name IS NULL LIMIT ?)""", batch);
                    if (n == 0) return sizes;
                    sizes.add(n);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Path dir = Path.of(args.length > 0 ? args[0] : "db");

        System.out.println("== The naive way: rename the column in one step while v1 still serves");
        World naive = new World(Files.createTempFile("naive", ".db"));
        naive.migrateTo(3, dir);
        naive.traffic(List.of(V1));
        try (Connection c = naive.open()) { update(c, "ALTER TABLE members RENAME COLUMN name TO full_name"); }
        System.out.println("v1 after the rename: " + naive.traffic(List.of(V1)).failed() + " of 20 requests failed");
        try (Connection c = naive.open()) {
            V1.nameOf(c, 1);
        } catch (SQLException e) {
            System.out.println("v1 sees: " + e.getMessage());
        }

        System.out.println("\n== Expand, migrate, contract: never more than two releases live");
        System.out.println("step                          live     failed wrong");
        World w = new World(Files.createTempFile("parallel", ".db"));
        w.migrateTo(3, dir);
        row("0 v1 only", w.traffic(List.of(V1)));
        w.migrateTo(4, dir);
        row("1 migrate V4 (expand)", w.traffic(List.of(V1)));
        row("2 roll out v2", w.traffic(List.of(V1, V2)));
        row("  v2 everywhere", w.traffic(List.of(V2)));
        List<Integer> batches = w.backfill(20);
        System.out.println("3 backfill: " + batches.stream().mapToInt(Integer::intValue).sum() + " rows in "
            + batches.size() + " batches " + batches);
        row("4 roll out v3", w.traffic(List.of(V2, V3)));
        row("  v3 everywhere", w.traffic(List.of(V3)));
        w.migrateTo(5, dir);
        row("5 migrate V5 (contract)", w.traffic(List.of(V3)));
        int right = 0;
        try (Connection c = w.open()) {
            for (var e : w.expected.entrySet()) if (e.getValue().equals(V3.nameOf(c, e.getKey()))) right++;
        }
        System.out.println("v3 reads " + right + " of " + w.expected.size() + " members correctly");

        System.out.println("\n== Three mistakes the steps protect you from");
        World early = new World(Files.createTempFile("early", ".db"));
        early.migrateTo(3, dir); early.traffic(List.of(V1)); early.migrateTo(4, dir);
        System.out.println("v1 and v3 live together: " + early.traffic(List.of(V1, V3)).wrong() + " wrong reads in 20 requests");
        try (Connection c = early.open()) {
            System.out.println("v3 before the backfill reads member 1 as: " + V3.nameOf(c, 1));
        }
        try {
            early.migrateTo(5, dir);
        } catch (Migrator.MigrationException e) {
            System.out.println("contract before the backfill: " + e.getMessage());
        }
    }

    static void row(String step, Traffic t) { System.out.println("%-29s %s".formatted(step, t)); }

    static int update(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            return ps.executeUpdate();
        }
    }

    static String one(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getString(1) : null; }
        }
    }

    static long lastId(Connection c) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
            rs.next();
            return rs.getLong(1);
        }
    }
}
