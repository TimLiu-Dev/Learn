import java.util.*;
import java.util.function.Supplier;
import java.util.zip.CRC32;

public class Practice {
    record Migration(int version, String script) {
        // Flyway stores a CRC32 for each SQL migration; we do the same.
        long checksum() {
            CRC32 crc = new CRC32();
            crc.update(script.getBytes());
            return crc.getValue();
        }
    }

    // A stand-in for SQLite. Don't change it.
    static class FakeDatabase {
        final Set<String> tables = new HashSet<>();
        final Map<Integer, Long> history = new HashMap<>();   // version -> checksum, like schema_version

        void run(String script) {
            for (String raw : script.split("\n")) {
                String line = raw.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "create" -> { if (!tables.add(parts[1])) throw new IllegalStateException("table " + parts[1] + " already exists"); }
                    case "drop" -> { if (!tables.remove(parts[1])) throw new IllegalStateException("no such table: " + parts[1]); }
                    default -> throw new IllegalStateException("syntax error near " + parts[0]);
                }
            }
        }

        <T> T transaction(Supplier<T> block) {
            Set<String> savedTables = new HashSet<>(tables);
            Map<Integer, Long> savedHistory = new HashMap<>(history);
            try {
                return block.get();
            } catch (RuntimeException e) {             // roll back: put everything back as it was
                tables.clear(); tables.addAll(savedTables);
                history.clear(); history.putAll(savedHistory);
                throw e;
            }
        }
    }

    static List<Integer> migrate(FakeDatabase db, List<Migration> migrations) {
        throw new UnsupportedOperationException("TODO: check the applied checksums, then apply the pending migrations in version order");
    }

    // CHECKS
    static final Migration V1 = new Migration(1, "create books");
    static final Migration V2 = new Migration(2, "create members\ncreate loans");
    static final Migration V3 = new Migration(3, "drop loans");

    public static void main(String[] args) {
        check("a fresh database gets every migration in version order", "[1, 2, 3] [books, members]", () -> {
            FakeDatabase db = new FakeDatabase();
            List<Integer> applied = migrate(db, List.of(V3, V1, V2));
            return applied + " " + new TreeSet<>(db.tables);
        });
        check("running it again applies nothing", "[]", () -> {
            FakeDatabase db = new FakeDatabase();
            migrate(db, List.of(V1, V2));
            return migrate(db, List.of(V1, V2)).toString();
        });
        check("a new migration is the only one applied", "[3]", () -> {
            FakeDatabase db = new FakeDatabase();
            migrate(db, List.of(V1, V2));
            return migrate(db, List.of(V1, V2, V3)).toString();
        });
        check("an edited migration is refused before anything runs", "refused V1 [books]", () -> {
            FakeDatabase db = new FakeDatabase();
            migrate(db, List.of(V1));
            try {
                migrate(db, List.of(new Migration(1, "create bookz"), V2));
                return "not refused";
            } catch (IllegalStateException e) {
                return (e.getMessage().contains("V1") ? "refused V1 " : "wrong message ") + new TreeSet<>(db.tables);
            }
        });
        check("a failing migration leaves no trace but earlier ones stay", "[books] [1]", () -> {
            FakeDatabase db = new FakeDatabase();
            try { migrate(db, List.of(V1, new Migration(2, "create members\nalter members"))); } catch (IllegalStateException e) { }
            return new TreeSet<>(db.tables) + " " + new TreeSet<>(db.history.keySet());
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
