package shelf.store;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class Db {
    // IMMEDIATE: a transaction takes SQLite's write lock at BEGIN, so writers queue
    // instead of failing with SQLITE_BUSY halfway through (L36).
    private final String url;

    public Db(Path file) {
        this.url = "jdbc:sqlite:" + file + "?transaction_mode=IMMEDIATE";
    }

    @FunctionalInterface
    public interface Work<T> {
        T run(Connection c) throws SQLException;
    }

    @FunctionalInterface
    public interface Row<T> {
        T map(ResultSet rs) throws SQLException;
    }

    /** SQLException is checked; the stores rethrow it unchecked so callers aren't forced to catch it. */
    public static final class DbException extends RuntimeException {
        DbException(SQLException cause) {
            super(cause);
        }
    }

    public Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(url);
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");               // off by default (L34)
        }
        return c;
    }

    public <T> T read(Work<T> work) {
        try (Connection c = connect()) {
            return work.run(c);
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public <T> T transaction(Work<T> work) {
        try (Connection c = connect()) {
            c.setAutoCommit(false);
            try {
                T result = work.run(c);
                c.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public static final List<String> SCHEMA = List.of(
        "CREATE TABLE IF NOT EXISTS authors (id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE)",
        """
        CREATE TABLE IF NOT EXISTS books (
          id        INTEGER PRIMARY KEY,
          title     TEXT NOT NULL,
          author_id INTEGER NOT NULL REFERENCES authors(id),
          year      INTEGER CHECK (year BETWEEN 1400 AND 2100))""",
        "CREATE TABLE IF NOT EXISTS members (id INTEGER PRIMARY KEY, name TEXT NOT NULL, email TEXT NOT NULL UNIQUE)",
        """
        CREATE TABLE IF NOT EXISTS loans (
          id        INTEGER PRIMARY KEY,
          book_id   INTEGER NOT NULL REFERENCES books(id),
          member_id INTEGER NOT NULL REFERENCES members(id),
          loaned    TEXT NOT NULL,
          returned  TEXT)""",
        // At most one open loan per book: the database itself refuses a double loan (L36).
        "CREATE UNIQUE INDEX IF NOT EXISTS one_open_loan_per_book ON loans(book_id) WHERE returned IS NULL",
        "CREATE INDEX IF NOT EXISTS loans_by_member ON loans(member_id)",
        "CREATE INDEX IF NOT EXISTS books_by_author ON books(author_id)");   // ?author= filter (Part 3)

    public void createSchema() {
        transaction(c -> {
            try (Statement st = c.createStatement()) {
                for (String sql : SCHEMA) st.executeUpdate(sql);
            }
            return null;
        });
    }

    // Small JDBC helpers: every value goes through a ? placeholder, never string concatenation (L33).
    public static <T> List<T> query(Connection c, String sql, Row<T> map, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, args);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> out = new ArrayList<>();
                while (rs.next()) out.add(map.map(rs));
                return out;
            }
        }
    }

    public static int update(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, args);
            return ps.executeUpdate();
        }
    }

    public static long insert(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, args);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    private static void bind(PreparedStatement ps, Object... args) throws SQLException {
        for (int i = 0; i < args.length; i++) ps.setObject(i + 1, args[i]);
    }
}
