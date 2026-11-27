import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// v1 renames by writing name only; v2 reads COALESCE(full_name, name).
public class StaleName {
    static void run(String label, Migrator.Migration v4) throws Exception {
        Path file = Files.createTempFile("stale", ".db");
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + file); Statement st = c.createStatement()) {
            List<Migrator.Migration> all = new ArrayList<>(Migrator.load(Path.of("db")).stream().filter(m -> m.version() <= 3).toList());
            all.add(v4);
            Migrator.migrate(c, all);
            st.executeUpdate("INSERT INTO members (name, full_name, email) VALUES ('Grace Hopper', 'Grace Hopper', 'g@example.org')"); // v2 adds
            st.executeUpdate("UPDATE members SET name = 'Grace B. Hopper' WHERE id = 1");                                            // v1 renames
            try (ResultSet rs = st.executeQuery("SELECT COALESCE(full_name, name) FROM members WHERE id = 1")) {
                rs.next();
                System.out.println("%-22s v2 reads: %s".formatted(label, rs.getString(1)));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Migrator.Migration plain = Migrator.load(Path.of("db")).stream().filter(m -> m.version() == 4).findFirst().orElseThrow();
        Migrator.Migration synced = Migrator.load(Path.of("ex/sync")).getFirst();
        run("V4 without a trigger", plain);
        run("V4 with the trigger", synced);
    }
}
