import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.stream.Stream;

public class LogDemo {
    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("l56");
        Path file = dir.resolve("data.log");

        try (LogKV db = new LogKV(file)) {
            db.put("dune", "Herbert");
            db.put("emma", "Austen");
            db.put("dune", "Frank Herbert");     // an overwrite is just another append
            db.delete("emma");                    // so is a delete: a tombstone
            System.out.println("get(dune) = " + db.get("dune") + ", get(emma) = " + db.get("emma"));
            System.out.println("index     = " + db.indexSnapshot());
            System.out.println("file      = " + db.fileSize() + " bytes, " + db.deadBytes() + " of them garbage");
        }

        byte[] bytes = Files.readAllBytes(file);
        System.out.println("record 0  = " + HexFormat.ofDelimiter(" ").formatHex(Arrays.copyOfRange(bytes, 0, 23)));
        System.out.println("            crc         keyLen=4    valLen=7    d  u  n  e  H  e  r  b  e  r  t");

        try (LogKV db = new LogKV(file)) {       // a new process would do exactly this: scan and rebuild
            System.out.println("reopened  : get(dune) = " + db.get("dune") + ", keys = " + db.keys());
        }

        try (LogKV db = new LogKV(dir.resolve("big.log"))) {
            for (int i = 0; i < 100_000; i++) db.put("key" + (i % 1_000), "value-" + i);
            System.out.println("100,000 puts to 1,000 keys: " + db.fileSize() / 1024 + " KB on disk, "
                    + db.deadBytes() * 100 / db.fileSize() + "% garbage");
            db.compact();
            System.out.println("after compact():           " + db.fileSize() / 1024 + " KB, get(key7) = " + db.get("key7"));
        }
        try (Stream<Path> s = Files.walk(dir)) {
            s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }
}
