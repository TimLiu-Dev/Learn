package shortener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/** SQLite storage (L34-L36). One connection per call, like L36. */
public class LinkStore {
    public record Link(String code, String url, Long expiresAt) {}

    public static class AliasTakenException extends RuntimeException {
        public AliasTakenException(String alias) { super("alias '" + alias + "' is taken"); }
    }

    private final String url;
    private final long simulatedLatencyMs;
    public final AtomicLong queries = new AtomicLong();

    public LinkStore(String dbFile) throws SQLException { this(dbFile, 0); }

    public LinkStore(String dbFile, long simulatedLatencyMs) throws SQLException {
        this.url = "jdbc:sqlite:" + dbFile + "?transaction_mode=IMMEDIATE&busy_timeout=5000&journal_mode=WAL";
        this.simulatedLatencyMs = simulatedLatencyMs;
        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS links (
                    id INTEGER PRIMARY KEY, code TEXT NOT NULL UNIQUE, url TEXT NOT NULL,
                    created_at INTEGER NOT NULL, expires_at INTEGER)""");
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS clicks_daily (
                    code TEXT NOT NULL, day TEXT NOT NULL, clicks INTEGER NOT NULL,
                    PRIMARY KEY (code, day)) WITHOUT ROWID""");
        }
    }

    private Connection connect() throws SQLException {
        queries.incrementAndGet();
        if (simulatedLatencyMs > 0) {
            try { Thread.sleep(simulatedLatencyMs); }                       // pretend the DB is across a network
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        return DriverManager.getConnection(url);
    }

    /** Random 7-char code, retrying on the UNIQUE constraint; or the caller's alias. */
    public Link create(String url, String alias, long now, Long expiresAt) throws SQLException {
        for (int attempt = 0; attempt < 5; attempt++) {
            String code = alias != null ? alias : Base62.random(7);
            try (Connection c = connect();
                 PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO links(code, url, created_at, expires_at) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, code);
                ps.setString(2, url);
                ps.setLong(3, now);
                if (expiresAt == null) ps.setNull(4, Types.INTEGER); else ps.setLong(4, expiresAt);
                ps.executeUpdate();
                return new Link(code, url, expiresAt);
            } catch (SQLException e) {
                if (e.getMessage() == null || !e.getMessage().contains("UNIQUE")) throw e;
                if (alias != null) throw new AliasTakenException(alias);   // retrying won't help
            }
        }
        throw new IllegalStateException("5 random codes in a row were taken");
    }

    public Link find(String code) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("SELECT url, expires_at FROM links WHERE code = ?")) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String target = rs.getString(1);
                long expires = rs.getLong(2);
                return new Link(code, target, rs.wasNull() ? null : expires);   // L36: wasNull
            }
        }
    }

    /** One transaction for a whole batch of clicks (L36 batching). */
    public void addClicks(String day, Map<String, Integer> counts) throws SQLException {
        try (Connection c = connect()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement("""
                    INSERT INTO clicks_daily VALUES (?, ?, ?)
                    ON CONFLICT(code, day) DO UPDATE SET clicks = clicks + excluded.clicks""")) {
                for (var e : counts.entrySet()) {
                    ps.setString(1, e.getKey());
                    ps.setString(2, day);
                    ps.setInt(3, e.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            c.commit();
        }
    }

    public long clicks(String code) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("SELECT coalesce(sum(clicks), 0) FROM clicks_daily WHERE code = ?")) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }
}
