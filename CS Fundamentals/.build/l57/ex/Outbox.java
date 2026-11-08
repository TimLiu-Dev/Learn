import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Exercise: the transactional outbox, on a real SQLite file.
// The order row and its "OrderPlaced" message commit in ONE local transaction;
// a relay publishes messages afterwards, and the consumer ignores duplicates.
public class Outbox {
    static class RelayCrash extends RuntimeException { RelayCrash() { super("relay crashed"); } }
    record Message(long id, String payload) {}

    static void placeOrder(Connection db, String item) throws SQLException {
        db.setAutoCommit(false);
        try (PreparedStatement ps = db.prepareStatement("INSERT INTO orders(item) VALUES (?)")) {
            ps.setString(1, item); ps.executeUpdate();
        }
        long id;
        try (Statement st = db.createStatement(); ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {
            rs.next(); id = rs.getLong(1);
        }
        try (PreparedStatement ps = db.prepareStatement("INSERT INTO outbox(payload) VALUES (?)")) {
            ps.setString(1, "OrderPlaced #" + id + " " + item); ps.executeUpdate();
        }
        db.commit();                                    // both rows, or neither
        db.setAutoCommit(true);
    }

    static void relay(Connection db, List<Message> broker, Long crashAfterPublishing) throws SQLException {
        List<Message> pending = new ArrayList<>();
        try (Statement st = db.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, payload FROM outbox WHERE sent = 0 ORDER BY id")) {
            while (rs.next()) pending.add(new Message(rs.getLong(1), rs.getString(2)));
        }
        for (Message m : pending) {
            broker.add(m);                              // 1. publish
            System.out.println("  relay published outbox #" + m.id());
            if (crashAfterPublishing != null && m.id() == crashAfterPublishing) throw new RelayCrash();
            try (PreparedStatement ps = db.prepareStatement("UPDATE outbox SET sent = 1 WHERE id = ?")) {
                ps.setLong(1, m.id()); ps.executeUpdate();   // 2. mark sent
            }
        }
    }

    static class EmailConsumer {
        private final Set<Long> seen = new HashSet<>();   // in production: a processed_messages table
        int emails = 0;
        void handle(Message m) {
            if (!seen.add(m.id())) { System.out.println("  consumer: #" + m.id() + " again, already handled, skip"); return; }
            emails++;
            System.out.println("  consumer: email for \"" + m.payload() + "\"");
        }
    }

    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile("outbox", ".db");
        try (Connection db = DriverManager.getConnection("jdbc:sqlite:" + file)) {
            try (Statement st = db.createStatement()) {
                st.executeUpdate("CREATE TABLE orders(id INTEGER PRIMARY KEY, item TEXT NOT NULL)");
                st.executeUpdate("CREATE TABLE outbox(id INTEGER PRIMARY KEY, payload TEXT NOT NULL, sent INTEGER NOT NULL DEFAULT 0)");
            }
            for (String item : List.of("lamp", "desk", "chair")) placeOrder(db, item);
            List<Message> broker = new ArrayList<>();
            System.out.println("== relay run 1 crashes after publishing #2, before marking it sent");
            try { relay(db, broker, 2L); } catch (RelayCrash e) { System.out.println("  " + e.getMessage()); }
            System.out.println("== relay run 2, after a restart");
            relay(db, broker, null);
            System.out.println("== the consumer reads the broker: " + broker.size() + " messages for 3 orders");
            EmailConsumer consumer = new EmailConsumer();
            broker.forEach(consumer::handle);
            System.out.println("emails sent: " + consumer.emails);
        }
        Files.delete(file);
    }
}
