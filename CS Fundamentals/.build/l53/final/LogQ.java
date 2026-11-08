import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

// The same five answers as logq.sh, in one pass over the file
public class LogQ {
    record Req(String method, String path, int status, int ms, String id, String client) {}

    // This log's lines are flat {"key":"text" or number} pairs, so a regex is enough here.
    // Real JSON (nesting, escapes) needs a parser: L32.
    static final Pattern FIELD = Pattern.compile("\"(\\w+)\":(\"[^\"]*\"|\\d+)");
    static final Pattern ID = Pattern.compile("/[0-9]+");

    static Req parse(String line) {
        Map<String, String> f = new HashMap<>();
        Matcher m = FIELD.matcher(line);
        while (m.find()) f.put(m.group(1), m.group(2).replace("\"", ""));
        return new Req(f.get("method"), f.get("path"), Integer.parseInt(f.get("status")),
            Integer.parseInt(f.get("ms")), f.get("requestId"), f.get("client"));
    }

    public static void main(String[] args) throws IOException {
        List<Req> reqs;
        try (Stream<String> lines = Files.lines(Path.of(args[0]))) {
            reqs = lines.map(LogQ::parse).toList();
        }
        System.out.println("== requests: " + reqs.size());

        System.out.println("== by status");
        new TreeMap<>(reqs.stream().collect(Collectors.groupingBy(Req::status, Collectors.counting())))
            .forEach((status, n) -> System.out.printf("%d %7d%n", status, n));

        System.out.println("== top 5 endpoints");
        reqs.stream()
            .collect(Collectors.groupingBy(r -> r.method() + " " + ID.matcher(r.path()).replaceAll("/{id}"), Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(5)
            .forEach(e -> System.out.printf("%7d %s%n", e.getValue(), e.getKey()));

        System.out.println("== 5 slowest requests");
        reqs.stream()
            .sorted(Comparator.comparingInt(Req::ms).reversed().thenComparing(Req::id)).limit(5)
            .forEach(r -> System.out.printf("%5d ms  %s %s  %s%n", r.ms(), r.method(), r.path(), r.id()));

        System.out.println("== p95 latency of GET /books/{id}");
        List<Integer> books = reqs.stream()
            .filter(r -> r.method().equals("GET") && r.path().matches("/books/[0-9]+"))
            .map(Req::ms).sorted().toList();
        System.out.println(books.get((books.size() * 95 + 99) / 100 - 1) + " ms over " + books.size() + " requests");

        System.out.println("== rate-limited (429) by client");
        reqs.stream().filter(r -> r.status() == 429)
            .collect(Collectors.groupingBy(Req::client, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> System.out.printf("%7d %s%n", e.getValue(), e.getKey()));
    }
}
