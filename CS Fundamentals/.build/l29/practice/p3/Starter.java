import java.util.*;
import java.util.stream.*;

// Nearest Java equivalent: Java has no Flow with operators, so this is a lazy Stream (L15).
// It is pulled one item at a time like a Flow, but each page call blocks instead of suspending.
public class Practice {
    interface SearchApi {
        List<String> page(int number);   // an empty page means "no more"
    }

    // Every item on every page, fetching a page only when the stream needs it.
    static Stream<String> allItems(SearchApi api) {
        throw new UnsupportedOperationException("TODO: a lazy stream of pages 1, 2, …, up to the first empty one");
    }

    // The first n items containing word (ignoring case), uppercased.
    static List<String> firstMatches(SearchApi api, String word, int n) {
        throw new UnsupportedOperationException("TODO: a stream pipeline on allItems(api)");
    }

    // CHECKS
    // Three items per page, then empty pages. It records every page number it was asked for.
    static class FakeSearch implements SearchApi {
        final List<String> items;
        final List<Integer> fetched = new ArrayList<>();
        FakeSearch(List<String> items) { this.items = items; }

        public List<String> page(int number) {
            fetched.add(number);
            int from = Math.min((number - 1) * 3, items.size());
            return items.subList(from, Math.min(from + 3, items.size()));
        }
    }

    static final List<String> FRUIT = List.of("apple", "Grape", "kiwi", "pear", "grapefruit", "plum", "fig", "grape juice", "lime", "lemon");

    public static void main(String[] args) {
        FakeSearch a = new FakeSearch(FRUIT);
        check("all items in order", FRUIT, () -> allItems(a).toList());
        check("then stop at the empty page", List.of(1, 2, 3, 4, 5), () -> a.fetched);
        FakeSearch b = new FakeSearch(FRUIT);
        check("nothing is fetched until a terminal operation", List.of(), () -> { allItems(b); return b.fetched; });
        check("the first item needs only page 1", List.of(1), () -> { allItems(b).findFirst(); return b.fetched; });
        check("first matches, uppercased", List.of("GRAPE", "GRAPEFRUIT"), () -> firstMatches(new FakeSearch(FRUIT), "grape", 2));
        FakeSearch c = new FakeSearch(FRUIT);
        check("only the pages needed are fetched", List.of(1, 2), () -> { firstMatches(c, "grape", 2); return c.fetched; });
        check("fewer matches than asked for", List.of("LEMON"), () -> firstMatches(new FakeSearch(FRUIT), "lem", 5));
        check("no results at all", List.of(), () -> firstMatches(new FakeSearch(List.of()), "x", 3));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
