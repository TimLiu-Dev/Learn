import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class LsmDemo {
    static Lsm db;
    static Map<String, String> latest = new HashMap<>();             // the truth, to check against

    static void probe(String label, List<String> keys) throws IOException {
        db.stats.blocksRead = 0; db.stats.bloomSkips = 0;
        int wrong = 0;
        for (String k : keys) if (!Objects.equals(db.get(k), latest.get(k))) wrong++;
        System.out.println(String.format("%-28s blocks read per get %5.2f  bloom skips per get %5.2f  wrong %d", label,
                (double) db.stats.blocksRead / keys.size(), (double) db.stats.bloomSkips / keys.size(), wrong));
    }

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("l56");
        db = new Lsm(dir, 10_000);
        Random rnd = new Random(56);
        for (int i = 0; i < 100_000; i++) {
            String key = String.format("user%05d", rnd.nextInt(50_000));
            db.put(key, "v" + i); latest.put(key, "v" + i);
        }
        db.delete("user00042"); latest.remove("user00042");
        db.flush();
        System.out.println(String.format("100,000 puts to %d keys -> %d SSTables, write amplification %.2f",
                latest.size(), db.tableCount(), (double) db.diskBytes() / db.userBytes()));

        List<String> present = new ArrayList<>(latest.keySet());
        Collections.shuffle(present, new Random(1));
        present = present.subList(0, 10_000);
        List<String> absent = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) absent.add(String.format("user%05d-x", i * 5));   // between real keys
        absent.add("user00042");
        probe("present keys", present);
        probe("absent keys, Bloom on", absent);
        db.useBloom = false;
        probe("absent keys, Bloom off", absent);
        db.useBloom = true;

        System.out.println("range(user01000, user01004) = " + db.range("user01000", "user01004"));

        db.compact();
        System.out.println(String.format("after compact: %d SSTable, write amplification %.2f",
                db.tableCount(), (double) db.diskBytes() / db.userBytes()));
        probe("present keys, compacted", present);
        try (Stream<Path> s = Files.walk(dir)) { s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
    }
}
