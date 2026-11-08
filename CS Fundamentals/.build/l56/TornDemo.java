import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class TornDemo {
    static List<String> keysAfterReopen(Path file, byte[] bytes) throws IOException {
        Files.write(file, bytes);
        try (LogKV db = new LogKV(file)) { return new ArrayList<>(db.indexSnapshot().keySet()); }
    }

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("l56");
        Path good = dir.resolve("good.log");
        try (LogKV db = new LogKV(good)) { for (int i = 1; i <= 3; i++) db.put("k" + i, "v" + i); }
        byte[] full = Files.readAllBytes(good);
        Path t = dir.resolve("t.log");

        // 1. A crash can cut the file anywhere. Try every length inside the last record.
        Map<List<String>, List<Integer>> byLength = new LinkedHashMap<>();
        for (int len = 32; len <= 48; len++)
            byLength.computeIfAbsent(keysAfterReopen(t, Arrays.copyOf(full, len)), k -> new ArrayList<>()).add(len);
        for (var e : byLength.entrySet()) {
            List<Integer> lens = e.getValue();
            System.out.println("length " + lens.getFirst() + ".." + lens.getLast() + " -> " + e.getKey());
        }

        // 2. The OS may grow the file before the data lands: garbage at the end.
        byte[] junk = new byte[16];
        new Random(56).nextBytes(junk);
        byte[] withJunk = Arrays.copyOf(full, full.length + junk.length);
        System.arraycopy(junk, 0, withJunk, full.length, junk.length);
        System.out.println("full + 16 junk bytes -> " + keysAfterReopen(t, withJunk));

        // 3. One flipped bit in the middle record.
        byte[] flipped = full.clone();
        flipped[16 + 13] ^= 1;
        System.out.println("bit flip in record 2 -> " + keysAfterReopen(t, flipped));

        // 4. The bug: reopen a torn file, then keep writing.
        Files.write(t, Arrays.copyOf(full, 40));         // 2 good records + 8 bytes of a torn third
        try (LogKV db = new LogKV(t)) { db.put("k4", "v4"); System.out.println("wrote k4, get(k4) = " + db.get("k4")); }
        try (LogKV db = new LogKV(t)) {
            System.out.println("reopen: keys = " + db.indexSnapshot().keySet() + ", get(k4) = " + db.get("k4"));
        }
        try (Stream<Path> s = Files.walk(dir)) { s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
    }
}
