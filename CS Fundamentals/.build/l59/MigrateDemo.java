import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MigrateDemo {
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("migrations");     // a copy of V1-V3 we're allowed to break
        try (Stream<Path> files = Files.list(Path.of(args.length > 0 ? args[0] : "db"))) {
            for (Path f : files.toList()) {
                if (f.getFileName().toString().matches("V[123]__.*")) Files.copy(f, dir.resolve(f.getFileName()));
            }
        }
        Path dbFile = Files.createTempFile("library", ".db");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile)) {
            exec(conn, "PRAGMA foreign_keys = ON");

            System.out.println("== 1. A fresh database");
            report(Migrator.migrate(conn, Migrator.load(dir)));
            exec(conn, "INSERT INTO members (name, email) VALUES ('Ada', 'ada@example.com')");

            System.out.println("== 2. The next start: nothing to do");
            report(Migrator.migrate(conn, Migrator.load(dir)));

            System.out.println("== 3. Someone edits V2 after it ran");
            Path v2 = dir.resolve("V2__add_joined.sql");
            String original = Files.readString(v2);
            Files.writeString(v2, original.replace("joined TEXT", "joined TEXT DEFAULT 'today'"));
            attempt(conn, dir);
            Files.writeString(v2, original);

            System.out.println("== 4. A new migration whose second statement fails");
            Files.writeString(dir.resolve("V4__add_fines.sql"),
                "CREATE TABLE fines (id INTEGER PRIMARY KEY, amount INTEGER);\nALTER TABLE members ADD COLUMN email TEXT;\n");
            attempt(conn, dir);
            System.out.println("fines table exists? " + tables(conn).contains("fines"));

            System.out.println("== 5. The history table");
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT version, description, checksum FROM schema_version ORDER BY version")) {
                while (rs.next()) System.out.println("V" + rs.getInt(1) + "  " + "%-14s %s".formatted(rs.getString(2), rs.getString(3)));
            }
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT sql FROM sqlite_schema WHERE name = 'members'")) {
                rs.next();
                System.out.println("members: " + rs.getString(1).replaceAll("\\s+", " "));
            }
        }
    }

    static void report(List<Migrator.Migration> applied) {
        System.out.println(applied.isEmpty() ? "applied nothing" : applied.stream()
            .map(m -> "applied V" + m.version() + " " + m.description()).collect(Collectors.joining("\n")));
    }

    static void attempt(Connection conn, Path dir) throws IOException, SQLException {
        try {
            report(Migrator.migrate(conn, Migrator.load(dir)));
        } catch (Migrator.MigrationException e) {
            System.out.println("REFUSED: " + e.getMessage());
        }
    }

    static List<String> tables(Connection conn) throws SQLException {
        List<String> names = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM sqlite_schema WHERE type = 'table'")) {
            while (rs.next()) names.add(rs.getString(1));
        }
        return names;
    }

    static void exec(Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement()) { st.execute(sql); }
    }
}
