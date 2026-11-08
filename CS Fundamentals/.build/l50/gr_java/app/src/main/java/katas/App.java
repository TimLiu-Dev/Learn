package katas;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class App {
    static Map<String, Integer> counts = new HashMap<>();

    // The L3 way, for comparison: a map of counts, scanned and sorted on every query.
    static List<Suggestion> scan(String p, int k) {
        return counts.entrySet().stream()
                .filter(e -> e.getKey().startsWith(p))
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .limit(k)
                .map(e -> new Suggestion(e.getKey(), e.getValue()))
                .toList();
    }

    public static void main(String[] args) throws IOException {
        String book;
        try (InputStream in = App.class.getResourceAsStream("/alice.txt")) {
            book = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        int start = book.indexOf("***", book.indexOf("*** START OF") + 3) + 3;
        String body = book.substring(start, book.indexOf("*** END OF"));

        Autocomplete ac = new Autocomplete();
        ac.addText(body);
        System.out.println(ac.size() + " distinct words");
        for (String p : List.of("th", "al", "wh", "qu", "x")) {
            String line = ac.suggest(p, 5).stream().map(s -> s.word() + " " + s.count()).collect(Collectors.joining(", "));
            System.out.printf("%-3s -> %s%n", p, line);
        }

        for (String w : body.toLowerCase().split("[^a-z]+")) if (!w.isEmpty()) counts.merge(w, 1, Integer::sum);
        List<String> prefixes = new ArrayList<>(new TreeSet<>(
                counts.keySet().stream().filter(w -> w.length() >= 2).map(w -> w.substring(0, 2)).toList()));
        for (String p : prefixes)
            if (!ac.suggest(p, 5).equals(scan(p, 5))) throw new IllegalStateException(p);

        long sink = 0;
        for (int r = 0; r < 5; r++) for (String p : prefixes) sink += scan(p, 5).size() + ac.suggest(p, 5).size();  // warm-up (L16)
        long t0 = System.nanoTime();
        for (int r = 0; r < 10; r++) for (String p : prefixes) sink += scan(p, 5).size();
        long t1 = System.nanoTime();
        for (int r = 0; r < 10; r++) for (String p : prefixes) sink += ac.suggest(p, 5).size();
        long t2 = System.nanoTime();
        int q = 10 * prefixes.size();
        System.out.println(prefixes.size() + " two-letter prefixes, same answers both ways");
        System.out.printf("scan + sort: %.1f µs per query%n", (t1 - t0) / 1000.0 / q);
        System.out.printf("trie + heap: %.1f µs per query%n", (t2 - t1) / 1000.0 / q);
        System.out.println("(sink " + sink + ")");
    }
}
