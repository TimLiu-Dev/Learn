import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

public class FsyncCost {
    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("l56");
        int n = 2_000;
        try (LogKV db = new LogKV(dir.resolve("warm.log"))) {          // warm-up (L16)
            for (int i = 0; i < 500; i++) { db.put("w" + i, "x"); db.sync(); }
        }
        var runs = new java.util.LinkedHashMap<String, Integer>();
        runs.put("sync every put", 1); runs.put("sync every 10", 10); runs.put("sync every 100", 100); runs.put("never sync", 0);
        for (Map.Entry<String, Integer> run : runs.entrySet()) {
            int every = run.getValue();
            try (LogKV db = new LogKV(dir.resolve(every + ".log"))) {
                long t0 = System.nanoTime();
                for (int i = 1; i <= n; i++) {
                    db.put("k" + i, "value-" + i);
                    if (every > 0 && i % every == 0) db.sync();
                }
                double us = (System.nanoTime() - t0) / 1000.0 / n;
                System.out.println(String.format("%-15s %8.1f µs per put  %,10d puts/s", run.getKey(), us, (long) (1_000_000 / us)));
            }
        }
        try (Stream<Path> s = Files.walk(dir)) { s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
    }
}
