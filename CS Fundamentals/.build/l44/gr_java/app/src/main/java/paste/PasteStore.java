package paste;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.concurrent.atomic.AtomicLong;

public class PasteStore {
    public record Row(String content, Long expiresAt) {}

    private final String url;
    public final AtomicLong queries = new AtomicLong();

    public PasteStore(String dbFile) throws SQLException {
        url = "jdbc:sqlite:" + dbFile + "?busy_timeout=5000&journal_mode=WAL";
        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS pastes (
                    id TEXT PRIMARY KEY, content TEXT NOT NULL,
                    created_at INTEGER NOT NULL, expires_at INTEGER)""");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS pastes_expiring ON pastes(expires_at) WHERE expires_at IS NOT NULL");
        }
    }

    private Connection connect() throws SQLException {
        queries.incrementAndGet();
        return DriverManager.getConnection(url);
    }

    /** false if the id is taken (the caller picks another). */
    public boolean insert(String id, String content, long now, Long expiresAt) throws SQLException {
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("INSERT INTO pastes VALUES (?, ?, ?, ?)")) {
            ps.setString(1, id);
            ps.setString(2, content);
            ps.setLong(3, now);
            if (expiresAt == null) ps.setNull(4, Types.INTEGER); else ps.setLong(4, expiresAt);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) return false;
            throw e;
        }
    }

    public Row find(String id) throws SQLException {
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("SELECT content, expires_at FROM pastes WHERE id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String content = rs.getString(1);
                long expires = rs.getLong(2);
                return new Row(content, rs.wasNull() ? null : expires);
            }
        }
    }

    public int deleteExpired(long now) throws SQLException {
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("DELETE FROM pastes WHERE expires_at <= ?")) {
            ps.setLong(1, now);
            return ps.executeUpdate();
        }
    }
}
