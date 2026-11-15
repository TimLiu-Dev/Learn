import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    record Count(int n, String line) { }

    // cut -d"delim" -f"field"
    static List<String> cut(List<String> lines, char delim, int field) {
        throw new UnsupportedOperationException("TODO: split each line on delim; a line without delim passes through whole");
    }

    // uniq -c: only ADJACENT equal lines are merged
    static List<Count> uniqC(List<String> lines) {
        throw new UnsupportedOperationException("TODO: extend the current run, or start a new one");
    }

    // sort | uniq -c | sort -rn | head -n n   (equal counts: alphabetical)
    static List<Count> topN(List<String> lines, int n) {
        throw new UnsupportedOperationException("TODO: sort, uniqC, count descending, first n");
    }

    // CHECKS
    public static void main(String[] args) {
        check("cut takes the second comma field", List.of("42", "7"), () -> cut(List.of("ann,42,x", "bob,7"), ',', 2));
        check("cut passes a line without the delimiter through", List.of("nodelim", ""), () -> cut(List.of("nodelim", "a,b"), ',', 5));
        check("cut sees an empty field between two spaces", List.of(""), () -> cut(List.of("GET  /books 200"), ' ', 2));
        check("uniqC merges only adjacent lines", List.of(new Count(2, "a"), new Count(1, "b"), new Count(1, "a")), () -> uniqC(List.of("a", "a", "b", "a")));
        check("topN counts every path, not every run", List.of(new Count(3, "/books"), new Count(2, "/health")),
              () -> topN(List.of("/health", "/books", "/authors", "/books", "/health", "/books"), 2));
        check("topN keeps equal counts alphabetical", List.of(new Count(2, "/c"), new Count(1, "/a"), new Count(1, "/b")),
              () -> topN(List.of("/b", "/c", "/a", "/c"), 10));
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
