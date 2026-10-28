import java.util.*;
import java.util.stream.Collectors;

// Exercise 2: the L40 bug (a cache key that ignores the query), then a canonical key:
// the same parameters in any order share one entry; different values never do.
public class Ex2 {
    static int dbCalls = 0;

    static List<String> searchDb(int page, int size) {          // the slow query
        dbCalls++;
        var titles = new ArrayList<String>();
        for (int i = 0; i < size; i++) titles.add("Book " + ((page - 1) * size + i + 1));
        return titles;
    }

    static Map<String, String> parse(String query) {
        var params = new TreeMap<String, String>();            // sorted by name
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            params.put(kv[0].trim().toLowerCase(), kv.length > 1 ? kv[1].trim() : "");
        }
        return params;
    }

    static String canonicalKey(String path, String query) {
        return path + "?" + parse(query).entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
    }

    static final Map<String, List<String>> cache = new HashMap<>();

    static List<String> search(String query, boolean buggy) {
        String key = buggy ? "books" : canonicalKey("/books", query);
        return cache.computeIfAbsent(key, k -> {
            var p = parse(query);
            return searchDb(Integer.parseInt(p.get("page")), Integer.parseInt(p.get("size")));
        });
    }

    public static void main(String[] args) {
        String[] requests = {"page=2&size=3", "size=3&page=2", "Page=2&SIZE=3", "page=3&size=3"};
        for (boolean buggy : new boolean[] {true, false}) {
            cache.clear(); dbCalls = 0;
            System.out.println(buggy ? "key = \"books\" (the L40 bug)" : "key = canonical path + sorted params");
            for (String q : requests) System.out.printf("  %-15s -> %s%n", q, search(q, buggy));
            System.out.println("  db calls: " + dbCalls + ", keys: " + cache.keySet());
        }
    }
}
