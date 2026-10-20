import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class LogAnalyzer {
    static final String[] PATHS = {"/api/users", "/api/orders", "/api/products", "/api/search",
                                   "/api/login", "/api/cart", "/api/checkout", "/health"};
    static final int[] BASE_MS = {8, 20, 12, 35, 25, 10, 60, 1};   // typical latency per path
    static final int LINES = 1_200_000;

    static long rssMb() throws IOException {          // ask the OS, via ps, how much RAM we hold
        Process ps = new ProcessBuilder("ps", "-o", "rss=", "-p", String.valueOf(ProcessHandle.current().pid())).start();
        try (BufferedReader r = ps.inputReader()) {
            return Long.parseLong(r.readLine().trim()) / 1024;
        }
    }

    static void generate(Path file) throws IOException {
        Random rnd = new Random(22);
        try (BufferedWriter out = Files.newBufferedWriter(file)) {
            for (int i = 0; i < LINES; i++) {
                int s = i / 14;                           // about 14 requests per second
                int p = Math.min(rnd.nextInt(10), PATHS.length - 1); // /health gets 3 in 10
                String method = rnd.nextInt(4) == 0 ? "POST" : "GET";
                int roll = rnd.nextInt(100);
                int status = roll <= 1 ? 500 : roll <= 6 ? 404 : 200;
                int ms = BASE_MS[p] + rnd.nextInt(30) + (rnd.nextInt(12) == 0 ? rnd.nextInt(900) : 0);
                out.write(String.format("2026-10-20T%02d:%02d:%02d %s %s %d %dms\n",
                        s / 3600 % 24, s / 60 % 60, s % 60, method, PATHS[p], status, ms));
            }
        }
    }

    static int p95(List<Integer> sorted) { return sorted.get((int) Math.ceil(0.95 * sorted.size()) - 1); } // nearest rank

    interface IoSupplier<T> { T get() throws IOException; }

    static <T> T timed(String label, IoSupplier<T> block) throws IOException {
        long t = System.nanoTime();
        T result = block.get();
        System.out.printf("%-28s %7.0f ms%n", label, (System.nanoTime() - t) / 1e6);
        return result;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("RSS at start: " + rssMb() + " MB");
        Path file = Files.createTempFile("access", ".log");
        generate(file);
        System.out.println("log: " + LINES + " lines, " + Files.size(file) / 1024 / 1024 + " MB");

        // 1. Unbuffered: one system call per byte. Only the first 1 MB, or we'd wait minutes.
        int oneMb = 1024 * 1024;
        long t1 = System.nanoTime();
        int newlines = timed("unbuffered, first 1 MB", () -> {
            try (FileInputStream in = new FileInputStream(file.toFile())) {
                int count = 0;
                for (int i = 0; i < oneMb; i++) if (in.read() == '\n') count++;
                return count;
            }
        });
        double wholeSec = (System.nanoTime() - t1) / 1e9 * Files.size(file) / oneMb;
        System.out.printf("  (%d lines; at that speed the whole file would take ~%.0f s)%n", newlines, wholeSec);

        // 2. Buffered, line by line, with a mutable map.
        Map<String, List<Integer>> byLoop = timed("buffered readLine loop", () -> {
            Map<String, List<Integer>> stats = new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(file)) {
                String line;
                while ((line = r.readLine()) != null) {
                    String[] parts = line.split(" ");
                    String ms = parts[4];
                    stats.computeIfAbsent(parts[2], k -> new ArrayList<>())
                         .add(Integer.parseInt(ms.substring(0, ms.length() - 2)));
                }
            }
            return stats;
        });

        // 3. Lazy pipeline: Files.lines is a Stream that holds the file open, so close it.
        Map<String, List<Integer>> byPipeline = timed("Files.lines pipeline", () -> {
            try (Stream<String> lines = Files.lines(file)) {
                return lines.map(l -> l.split(" "))
                        .collect(Collectors.groupingBy(p -> p[2],
                                Collectors.mapping(p -> Integer.parseInt(p[4].substring(0, p[4].length() - 2)),
                                        Collectors.toList())));
            }
        });
        if (!byLoop.equals(byPipeline)) throw new IllegalStateException("the two readers disagree");

        System.out.println("top 5 paths:");
        byPipeline.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<String, List<Integer>> e) -> e.getValue().size()).reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(5)
                .forEach(e -> {
                    List<Integer> sorted = e.getValue().stream().sorted().toList();
                    System.out.printf("  %-14s %7d requests  p95 %4d ms%n", e.getKey(), sorted.size(), p95(sorted));
                });

        System.out.println("RSS at end: " + rssMb() + " MB");
        Files.delete(file);
    }
}
