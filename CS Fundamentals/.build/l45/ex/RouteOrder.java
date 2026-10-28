import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Exercise 1: when two templates match one path, which wins?
// Shelf's Router takes the FIRST registered match; Spring ranks by specificity instead.
public class RouteOrder {
    record Hit(String template, Map<String, String> params) {
        @Override public String toString() { return "(" + template + ", " + params + ")"; }
    }

    static List<String> segments(String path) {
        return Arrays.stream(path.split("/")).filter(s -> !s.isEmpty()).toList();
    }

    static Map<String, String> bind(String template, String path) {
        List<String> t = segments(template), p = segments(path);
        if (t.size() != p.size()) return null;
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < t.size(); i++) {
            if (t.get(i).startsWith("{")) params.put(t.get(i).replaceAll("[{}]", ""), p.get(i));
            else if (!t.get(i).equals(p.get(i))) return null;
        }
        return params;
    }

    static Optional<Hit> hit(String template, String path) {
        return Optional.ofNullable(bind(template, path)).map(params -> new Hit(template, params));
    }

    // What Shelf's Router does: registration order decides.
    static Optional<Hit> matchFirst(List<String> templates, String path) {
        return templates.stream().map(t -> hit(t, path)).flatMap(Optional::stream).findFirst();
    }

    // The fix: fewest path variables wins, like Spring's PathPattern.SPECIFICITY_COMPARATOR.
    static Optional<Hit> matchMostSpecific(List<String> templates, String path) {
        return templates.stream().map(t -> hit(t, path)).flatMap(Optional::stream)
            .min(Comparator.comparingInt(h -> h.params().size()));
    }

    public static void main(String[] args) {
        var templateFirst = List.of("/books/{id}", "/books/count");
        var literalFirst = List.of("/books/count", "/books/{id}");
        System.out.println("first match,   template registered first: " + matchFirst(templateFirst, "/books/count").orElseThrow());
        System.out.println("first match,   literal registered first:  " + matchFirst(literalFirst, "/books/count").orElseThrow());
        System.out.println("most specific, template registered first: " + matchMostSpecific(templateFirst, "/books/count").orElseThrow());
        System.out.println("most specific, any path still binds:      " + matchMostSpecific(templateFirst, "/books/42").orElseThrow());
    }
}
