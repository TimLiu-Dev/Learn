import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Ex3 {
    sealed interface Parsed permits PageParams, Invalid {}
    record PageParams(int limit, int offset) implements Parsed {}
    record Invalid(List<String> errors) implements Parsed {}

    static Map<String, String> queryMap(String query) {
        if (query == null) return Map.of();
        return Arrays.stream(query.split("&")).filter(p -> p.contains("="))
            .collect(Collectors.toMap(p -> p.substring(0, p.indexOf('=')), p -> p.substring(p.indexOf('=') + 1)));
    }

    static Integer parseInt(String raw) {
        try { return Integer.parseInt(raw); } catch (NumberFormatException e) { return null; }
    }

    static Parsed parsePage(String query) {
        var q = queryMap(query);
        var errors = new ArrayList<String>();
        int limit = 10, offset = 0;
        if (q.containsKey("limit")) {
            Integer v = parseInt(q.get("limit"));
            if (v == null || v < 1 || v > 100) errors.add("limit must be 1..100, got '" + q.get("limit") + "'");
            else limit = v;
        }
        if (q.containsKey("offset")) {
            Integer v = parseInt(q.get("offset"));
            if (v == null || v < 0) errors.add("offset must be >= 0, got '" + q.get("offset") + "'");
            else offset = v;
        }
        return errors.isEmpty() ? new PageParams(limit, offset) : new Invalid(errors);
    }

    public static void main(String[] args) {
        for (String q : Arrays.asList(null, "limit=5", "limit=5&offset=20", "limit=500", "limit=two&offset=-1", "sort=title")) {
            System.out.printf("%-22s -> %s%n", q, parsePage(q));
        }
    }
}
