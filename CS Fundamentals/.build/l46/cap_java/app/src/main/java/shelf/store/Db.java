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
import java.util.concurrent.ArrayBlockingQueue;

public final class Db implements AutoCloseable {
    // IMMEDIATE: a transaction takes SQLite's write lock at BEGIN, so writers queue
    // instead of failing with SQLITE_BUSY halfway through (L36).
    // busy_timeout: how long a queued writer waits for that lock before giving up with SQLITE_BUSY.
    private final String url;
    // poolSize 0 = a fresh connection per call (v1). Otherwise a fixed set of open connections,
    // handed out through a BlockingQueue (L27): take() waits when all are busy, so it also caps concurrency.
    private final ArrayBlockingQueue<Connection> pool;

    public Db(Path file) {
        this(file, 0, 3000);
    }

    public Db(Path file, int poolSize, int busyTimeoutMs) {
        this.url = "jdbc:sqlite:" + file + "?transaction_mode=IMMEDIATE&busy_timeout=" + busyTimeoutMs;
        if (poolSize == 0) {
            this.pool = null;
            return;
        }
        this.pool = new ArrayBlockingQueue<>(poolSize);
        try {
            for (int i = 0; i < poolSize; i++) pool.add(connect());
        } catch (SQLException e) {
            throw new DbException(e);
        }
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

    private Connection borrow() throws SQLException {
        if (pool == null) return connect();
        try {
            return pool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("interrupted while waiting for a connection", e);
        }
    }

    private void release(Connection c) throws SQLException {
        if (pool == null) {
            c.close();
            return;
        }
        if (!c.getAutoCommit()) c.setAutoCommit(true);           // hand it back clean
        pool.add(c);
    }

    public <T> T read(Work<T> work) {
        try {
            Connection c = borrow();
            try {
                return work.run(c);
            } finally {
                release(c);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public <T> T transaction(Work<T> work) {
        try {
            Connection c = borrow();
            try {
                c.setAutoCommit(false);
                try {
                    T result = work.run(c);
                    c.commit();
                    return result;
                } catch (SQLException | RuntimeException e) {
                    // If BEGIN itself failed (e.g. SQLITE_BUSY), rollback() throws "no transaction is active"
                    // and would hide the real error. Keep the real one; attach the other as suppressed (L10).
                    try {
                        c.rollback();
                    } catch (SQLException r) {
                        e.addSuppressed(r);
                    }
                    throw e;
                }
            } finally {
                release(c);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    /** For /health: can we still run a query? */
    public boolean ping() {
        try {
            return read(c -> query(c, "SELECT 1", rs -> rs.getInt(1))).equals(List.of(1));
        } catch (RuntimeException e) {
            return false;
        }
    }

    /** For /metrics: idle pooled connections, or -1 without a pool. */
    public int idleConnections() {
        return pool == null ? -1 : pool.size();
    }

    @Override
    public void close() {
        if (pool == null) return;
        for (Connection c : pool) {
            try {
                c.close();
            } catch (SQLException ignored) {
                // closing anyway
            }
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
