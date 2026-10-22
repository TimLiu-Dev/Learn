package kv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class SqliteKvStore implements KvStore, AutoCloseable {
    private final Connection c;

    public SqliteKvStore(String url) throws SQLException {
        c = DriverManager.getConnection(url);
        try (Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS kv (k TEXT PRIMARY KEY, v TEXT NOT NULL)");
        }
    }

    public void put(String key, String value) {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO kv (k, v) VALUES (?, ?) ON CONFLICT(k) DO UPDATE SET v = excluded.v")) {
            ps.setString(1, key); ps.setString(2, value); ps.executeUpdate();
        } catch (SQLException e) { throw new IllegalStateException(e); }
    }

    public String get(String key) {
        try (PreparedStatement ps = c.prepareStatement("SELECT v FROM kv WHERE k = ?")) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getString(1) : null; }
        } catch (SQLException e) { throw new IllegalStateException(e); }
    }

    public boolean delete(String key) {
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM kv WHERE k = ?")) {
            ps.setString(1, key);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { throw new IllegalStateException(e); }
    }

    public List<String> keys() {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT k FROM kv ORDER BY k")) {
            List<String> out = new ArrayList<>();
            while (rs.next()) out.add(rs.getString(1));
            return out;
        } catch (SQLException e) { throw new IllegalStateException(e); }
    }

    public void close() throws SQLException { c.close(); }
}
