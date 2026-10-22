import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

// A service that builds its own dependencies: a real database file and the real calendar.
public class Hardwired {
    static final class LendingService {
        private final Path file = Path.of(System.getProperty("java.io.tmpdir"), "l38-library-java.db");
        private final Connection conn;

        LendingService() throws SQLException {
            conn = DriverManager.getConnection("jdbc:sqlite:" + file);
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS loans (member_id INTEGER, loaned TEXT, returned TEXT)");
            }
        }

        void recordLoan(long memberId, LocalDate loaned) throws SQLException {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO loans VALUES (?, ?, NULL)")) {
                ps.setLong(1, memberId); ps.setString(2, loaned.toString()); ps.executeUpdate();
            }
        }

        // Blocked if any open loan is older than 14 days, measured from TODAY, whenever "today" is.
        boolean mayBorrow(long memberId) throws SQLException {
            LocalDate cutoff = LocalDate.now().minusDays(14);
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM loans WHERE member_id = ? AND returned IS NULL AND loaned < ?")) {
                ps.setLong(1, memberId); ps.setString(2, cutoff.toString());
                try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1) == 0; }
            }
        }

        void close() throws Exception { conn.close(); Files.deleteIfExists(file); }
    }

    public static void main(String[] args) throws Exception {
        // A "test": a member with a loan from 2026-10-10 should still be allowed to borrow.
        LendingService service = new LendingService();   // can't choose the file, can't choose the date
        try {
            service.recordLoan(1, LocalDate.parse("2026-10-10"));
            System.out.println("today, per the system clock: " + LocalDate.now());
            System.out.println("member 1 may borrow: " + service.mayBorrow(1) + "   (expected true)");
            System.out.println("the same check flips to false from 2026-10-25 on, with no code change");
        } finally {
            service.close();
        }
    }
}
