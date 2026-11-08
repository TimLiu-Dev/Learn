import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class AutoCompact {
    // LogKV that compacts itself once garbage is more than half of a file bigger than minBytes.
    static final class AutoKV implements AutoCloseable {
        private final LogKV db;
        private final long minBytes;
        int compactions = 0;
        long largestFile = 0;

        AutoKV(Path path, long minBytes) throws IOException { this.db = new LogKV(path); this.minBytes = minBytes; }

        void put(String key, String value) throws IOException { db.put(key, value); afterWrite(); }
        void delete(String key) throws IOException { db.delete(key); afterWrite(); }
        String get(String key) throws IOException { return db.get(key); }
        long fileSize() throws IOException { return db.fileSize(); }

        private void afterWrite() throws IOException {
            largestFile = Math.max(largestFile, db.fileSize());
            if (db.fileSize() >= minBytes && db.deadBytes() * 2 > db.fileSize()) { db.compact(); compactions++; }
        }

        @Override public void close() throws IOException { db.close(); }
    }

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("l56");
        try (AutoKV db = new AutoKV(dir.resolve("auto.log"), 64 * 1024)) {
            for (int i = 0; i < 100_000; i++) db.put("key" + (i % 1_000), "value-" + i);
            System.out.println("compactions " + db.compactions + ", largest file " + db.largestFile / 1024 + " KB, now "
                    + db.fileSize() / 1024 + " KB, get(key7) = " + db.get("key7"));
        }
        try (Stream<Path> s = Files.walk(dir)) { s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
    }
}
