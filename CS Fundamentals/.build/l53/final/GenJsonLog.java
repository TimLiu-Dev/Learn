import java.io.*;
import java.nio.file.*;
import java.util.*;

// Writes access.jsonl: one JSON object per line, the shape L46's server will log
public class GenJsonLog {
    public static void main(String[] args) throws IOException {
        int lines = args.length > 0 ? Integer.parseInt(args[0]) : 200_000;
        List<String> clients = List.of("ada", "grace", "linus", "bot", "bot", "bot");
        Random rnd = new Random(530);                         // same seed, same bytes as GenJsonLog.kt
        try (BufferedWriter out = Files.newBufferedWriter(Path.of("access.jsonl"))) {
            for (int i = 0; i < lines; i++) {
                int roll = rnd.nextInt(100);
                String method = "GET", path;
                if (roll < 20) path = "/books";
                else if (roll < 55) path = "/books/" + (1 + rnd.nextInt(500));
                else if (roll < 60) { method = "POST"; path = "/books"; }
                else if (roll < 70) path = "/members/" + (1 + rnd.nextInt(80));
                else if (roll < 80) { method = "POST"; path = "/loans"; }
                else if (roll < 85) { method = "POST"; path = "/loans/" + (1 + rnd.nextInt(3000)) + "/return"; }
                else if (roll < 95) path = "/health";
                else path = "/metrics";
                String client = clients.get(rnd.nextInt(clients.size()));
                int r = rnd.nextInt(1000);
                int status;
                if (r < 10) status = 500;
                else if (client.equals("bot") && r < 60) status = 429;
                else if (path.startsWith("/books/") && r < 110) status = 404;
                else if (path.equals("/loans") && r < 200) status = 409;
                else if (method.equals("POST") && !path.endsWith("/return")) status = 201;
                else status = 200;
                int ms = rnd.nextInt(8) + (path.startsWith("/books") ? 10 : 2)
                    + (rnd.nextInt(50) == 0 ? 200 + rnd.nextInt(1800) : 0);
                long t = i * 50L;                                 // a request every 50 ms from 09:00
                String ts = String.format("2026-11-06T%02d:%02d:%02d.%03dZ", 9 + t / 3_600_000, t / 60_000 % 60, t / 1000 % 60, t % 1000);
                String id = String.format("%016x", rnd.nextLong());
                out.write("{\"ts\":\"" + ts + "\",\"level\":\"INFO\",\"requestId\":\"" + id + "\",\"method\":\"" + method
                    + "\",\"path\":\"" + path + "\",\"status\":" + status + ",\"ms\":" + ms + ",\"client\":\"" + client + "\"}");
                out.write("\n");
            }
        }
        System.out.println("wrote " + lines + " lines to access.jsonl");
    }
}
