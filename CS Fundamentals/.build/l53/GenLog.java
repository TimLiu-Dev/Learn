import java.io.*;
import java.nio.file.*;
import java.util.*;

// Writes requests.log in L22's format: "2026-11-06T09:00:01 GET /api/users 200 12ms"
public class GenLog {
    public static void main(String[] args) throws IOException {
        int lines = args.length > 0 ? Integer.parseInt(args[0]) : 1_000_000;
        List<String> paths = List.of("/health", "/api/books", "/api/books", "/api/books", "/api/login",
            "/api/members", "/api/loans", "/api/loans", "/api/search", "/metrics");
        Random rnd = new Random(53);                     // same seed, same bytes as GenLog.kt
        try (BufferedWriter out = Files.newBufferedWriter(Path.of("requests.log"))) {
            for (int i = 0; i < lines; i++) {
                String path = paths.get(rnd.nextInt(paths.size()));
                String method = path.equals("/api/loans") && rnd.nextInt(3) == 0 ? "POST" : "GET";
                int roll = rnd.nextInt(100);
                int status = roll < 2 ? 500 : roll < 5 ? 404 : method.equals("POST") ? 201 : 200;
                int ms = 2 + rnd.nextInt(40) + (rnd.nextInt(20) == 0 ? rnd.nextInt(900) : 0);
                int s = i % 60, m = i / 60 % 60, h = 9 + i / 3600 % 12;
                out.write(String.format("2026-11-06T%02d:%02d:%02d %s %s %d %dms\n", h, m, s, method, path, status, ms));
            }
        }
        System.out.println("wrote " + lines + " lines to requests.log");
    }
}
