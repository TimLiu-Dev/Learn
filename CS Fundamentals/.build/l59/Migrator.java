import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** A forward-only migration runner for SQLite over plain JDBC. */
public final class Migrator {
    private Migrator() { }

    /** One numbered SQL script: V3__unique_email.sql is version 3, "unique email". */
    public record Migration(int version, String description, String sql) {
        public String checksum() {
            try {
                byte[] hash = MessageDigest.getInstance("SHA-256").digest(sql.getBytes(StandardCharsets.UTF_8));
                return HexFormat.of().formatHex(hash).substring(0, 16);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);       // every JDK has SHA-256
            }
        }
    }

    public static class MigrationException extends RuntimeException {
        public MigrationException(String message) { super(message); }
    }

    private static final Pattern FILE_NAME = Pattern.compile("V(\\d+)__(\\w+)\\.sql");

    /** Reads every V<n>__<words>.sql file in dir, sorted by version as a number. */
    public static List<Migration> load(Path dir) throws IOException {
        List<Migration> found = new ArrayList<>();
        try (Stream<Path> files = Files.list(dir)) {
            for (Path file : files.toList()) {
                Matcher m = FILE_NAME.matcher(file.getFileName().toString());
                if (m.matches()) {
                    found.add(new Migration(Integer.parseInt(m.group(1)), m.group(2).replace('_', ' '), Files.readString(file)));
                }
            }
        }
        found.sort(Comparator.comparingInt(Migration::version));
        for (int i = 1; i < found.size(); i++) {
            if (found.get(i).version() == found.get(i - 1).version()) {
                throw new MigrationException("two migrations claim version " + found.get(i).version());
            }
        }
        return found;
    }

    /** Applies the pending migrations in order and returns them. Safe to call on every start. */
    public static List<Migration> migrate(Connection conn, List<Migration> migrations) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS schema_version (
                  version INTEGER PRIMARY KEY, description TEXT NOT NULL,
                  checksum TEXT NOT NULL, applied_at TEXT NOT NULL)""");
        }
        TreeMap<Integer, String> applied = new TreeMap<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT version, checksum FROM schema_version")) {
            while (rs.next()) applied.put(rs.getInt(1), rs.getString(2));
        }
        Map<Integer, Migration> known = migrations.stream()
            .collect(Collectors.toMap(Migration::version, m -> m));
        int newestKnown = migrations.stream().mapToInt(Migration::version).max().orElse(0);
        for (var row : applied.entrySet()) {
            int version = row.getKey();
            Migration m = known.get(version);
            // Newer code already migrated further. Older code keeps running (Part 2).
            if (m == null && version > newestKnown) continue;
            if (m == null) throw new MigrationException("V" + version + " was applied but its file is missing");
            if (!m.checksum().equals(row.getValue())) {
                throw new MigrationException("V" + version + " (" + m.description() + ") was edited after it ran: checksum "
                    + row.getValue() + " in the database, " + m.checksum() + " on disk");
            }
        }
        List<Migration> pending = migrations.stream().filter(m -> !applied.containsKey(m.version())).toList();
        int newestApplied = applied.isEmpty() ? 0 : applied.lastKey();
        for (Migration m : pending) {
            if (m.version() < newestApplied) {
                throw new MigrationException("V" + m.version() + " is older than V" + newestApplied + ", which already ran");
            }
        }
        for (Migration m : pending) apply(conn, m);
        return pending;
    }

    // Steps 1, 2, 10, 11 and 12 of SQLite's table-rebuild recipe wrap every migration,
    // so a migration file only has to contain steps 3 to 9.
    private static void apply(Connection conn, Migration m) throws SQLException {
        boolean fkWasOn;
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("PRAGMA foreign_keys")) {
            fkWasOn = rs.next() && rs.getInt(1) == 1;
        }
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = OFF");          // a no-op inside a transaction
        }
        conn.setAutoCommit(false);
        try {
            try (Statement st = conn.createStatement()) {
                for (String sql : statements(m.sql())) st.execute(sql);
                try (ResultSet rs = st.executeQuery("PRAGMA foreign_key_check")) {
                    if (rs.next()) throw new MigrationException("V" + m.version() + " broke a foreign key in table " + rs.getString(1));
                }
            }
            try (var ps = conn.prepareStatement("INSERT INTO schema_version VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, m.version()); ps.setString(2, m.description());
                ps.setString(3, m.checksum()); ps.setString(4, Instant.now().toString());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (MigrationException e) {
            conn.rollback();
            throw e;
        } catch (SQLException e) {
            conn.rollback();                                  // SQLite DDL is transactional: all of it, or none
            throw new MigrationException("V" + m.version() + " failed and was rolled back: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
            if (fkWasOn) {
                try (Statement st = conn.createStatement()) { st.execute("PRAGMA foreign_keys = ON"); }
            }
        }
    }

    /**
     * Splits a script into statements. A statement ends with a line ending in ";", except that a
     * CREATE TRIGGER runs on until a line ending in "END;". Lines starting with "--" are dropped.
     */
    public static List<String> statements(String sql) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String line : sql.lines().toList()) {
            if (line.stripLeading().startsWith("--")) continue;
            current.append(line).append('\n');
            String text = current.toString().strip();
            String upper = text.toUpperCase(Locale.ROOT);
            boolean ends = upper.startsWith("CREATE TRIGGER") ? upper.endsWith("END;") : text.endsWith(";");
            if (ends) {
                result.add(text.substring(0, text.length() - 1));
                current.setLength(0);
            }
        }
        if (!current.toString().isBlank()) result.add(current.toString().strip());
        return result;
    }
}
