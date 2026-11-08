package katas;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class LogStoreTest {
    @TempDir Path dir;

    private LogStore open() throws IOException { return LogStore.open(dir, Sync.NEVER); }
    private Path data() { return dir.resolve(LogStore.DATA); }

    @Test void emptyStoreHasNothing() throws IOException {
        try (LogStore db = open()) { assertNull(db.get("dune")); assertEquals(0, db.size()); }
    }

    @Test void putThenGet() throws IOException {
        try (LogStore db = open()) { db.put("dune", "Herbert"); assertEquals("Herbert", db.get("dune")); }
    }

    @Test void overwriteKeepsLatestValue() throws IOException {
        try (LogStore db = open()) {
            db.put("dune", "Herbert"); db.put("dune", "Frank Herbert");
            assertEquals("Frank Herbert", db.get("dune")); assertEquals(1, db.size());
        }
    }

    @Test void deleteSurvivesReopening() throws IOException {
        try (LogStore db = open()) { db.put("emma", "Austen"); db.delete("emma"); }
        try (LogStore db = open()) { assertNull(db.get("emma")); assertEquals(0, db.size()); }
    }

    @Test void reopeningRecoversEveryKey() throws IOException {
        try (LogStore db = LogStore.open(dir, Sync.EVERY_WRITE)) { for (int i = 0; i < 100; i++) db.put("k" + i, "v" + i); }
        try (LogStore db = open()) { assertEquals(100, db.size()); assertEquals("v42", db.get("k42")); }
    }

    @Test void tornTailIsCutAtEveryLengthAndNewWritesSurvive() throws IOException {
        try (LogStore db = open()) { for (int i = 1; i <= 3; i++) db.put("k" + i, "v" + i); }   // three 16-byte records
        byte[] full = Files.readAllBytes(data());
        for (int len = 33; len < full.length; len++) {
            Files.write(data(), Arrays.copyOf(full, len));
            try (LogStore db = open()) {
                Set<String> found = new TreeSet<>();
                for (int i = 1; i <= 3; i++) if (db.get("k" + i) != null) found.add("k" + i);
                assertEquals(Set.of("k1", "k2"), found, "len " + len);
                db.put("k4", "v4");
            }
            try (LogStore db = open()) { assertEquals("v4", db.get("k4"), "write after repair lost, len " + len); }
        }
    }

    @Test void garbageAfterLastRecordIsIgnored() throws IOException {
        try (LogStore db = open()) { db.put("dune", "Herbert"); }
        byte[] junk = new byte[100];
        new Random(56).nextBytes(junk);
        byte[] good = Files.readAllBytes(data());
        byte[] both = Arrays.copyOf(good, good.length + junk.length);
        System.arraycopy(junk, 0, both, good.length, junk.length);
        Files.write(data(), both);
        try (LogStore db = open()) { assertEquals("Herbert", db.get("dune")); assertEquals(1, db.size()); }
        assertEquals(23, Files.size(data()));                         // the junk was truncated
    }

    @Test void compactShrinksFileAndKeepsLatestValues() throws IOException {
        try (LogStore db = open()) {
            for (int i = 0; i < 1_000; i++) db.put("key" + (i % 10), "value-" + i);
            db.delete("key3");
            long before = db.fileBytes();
            db.compact();
            assertTrue(db.fileBytes() < before / 50, db.fileBytes() + " vs " + before);
            assertEquals("value-999", db.get("key9")); assertNull(db.get("key3")); assertEquals(9, db.size());
            db.put("after", "compaction");
            assertEquals("compaction", db.get("after"));
        }
    }

    @Test void compactionFileLeftByCrashIsIgnored() throws IOException {
        try (LogStore db = open()) { db.put("dune", "Herbert"); }
        byte[] junk = new byte[500];
        new Random(1).nextBytes(junk);
        Files.write(dir.resolve(LogStore.COMPACT), junk);
        try (LogStore db = open()) { assertEquals("Herbert", db.get("dune")); }
        assertFalse(Files.exists(dir.resolve(LogStore.COMPACT)));
    }

    @Test void emptyKeyIsRejected() throws IOException {
        try (LogStore db = open()) { assertThrows(IllegalArgumentException.class, () -> db.put("", "x")); }
    }

    @Test void killDashNineLosesNoAcknowledgedWrite() throws Exception {
        String java = ProcessHandle.current().info().command().orElseThrow();
        Process child = new ProcessBuilder(java, "-cp", System.getProperty("java.class.path"), "katas.KillChild", dir.toString())
                .redirectErrorStream(true).start();
        BufferedReader out = child.inputReader();
        int acked = -1;
        while (acked < 2_000) acked = Integer.parseInt(out.readLine().replace("acked ", ""));
        child.toHandle().destroyForcibly();     // SIGKILL mid-write; Process.destroyForcibly() would also close our pipe
        assertEquals(137, child.waitFor());
        String line;
        while ((line = out.readLine()) != null) acked = Integer.parseInt(line.replace("acked ", ""));   // acks already in the pipe
        try (LogStore db = open()) {
            for (int i = 0; i <= acked; i++) assertEquals("v" + i, db.get("k" + i), "acked k" + i + " lost");
            System.out.println("acked " + (acked + 1) + ", recovered " + db.size());
        }
    }
}
