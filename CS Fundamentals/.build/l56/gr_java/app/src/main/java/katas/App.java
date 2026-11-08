package katas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("logstore");
        try (LogStore db = LogStore.open(dir)) {
            db.put("dune", "Herbert"); db.put("emma", "Austen"); db.put("dune", "Frank Herbert"); db.delete("emma");
            System.out.println("size " + db.size() + ", " + db.fileBytes() + " bytes on disk");
        }
        try (LogStore db = LogStore.open(dir)) {
            System.out.println("reopened: dune = " + db.get("dune") + ", emma = " + db.get("emma"));
            db.compact();
            System.out.println("compacted: " + db.fileBytes() + " bytes on disk");
        }
        try (Stream<Path> s = Files.walk(dir)) { s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
    }
}
