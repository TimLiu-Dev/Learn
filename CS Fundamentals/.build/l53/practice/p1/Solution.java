import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    record Count(int n, String line) { }

    // cut -d"delim" -f"field"
    static List<String> cut(List<String> lines, char delim, int field) {
        List<String> out = new ArrayList<>();
        for (String line : lines) {
            if (line.indexOf(delim) < 0) { out.add(line); continue; }
            // limit -1 keeps trailing empty fields, as cut does
            String[] parts = line.split(java.util.regex.Pattern.quote(String.valueOf(delim)), -1);
            out.add(field <= parts.length ? parts[field - 1] : "");
        }
        return out;
    }

    // uniq -c: only ADJACENT equal lines are merged
    static List<Count> uniqC(List<String> lines) {
        List<Count> runs = new ArrayList<>();
        for (String line : lines) {
            int last = runs.size() - 1;
            if (last >= 0 && runs.get(last).line().equals(line)) runs.set(last, new Count(runs.get(last).n() + 1, line));
            else runs.add(new Count(1, line));
        }
        return runs;
    }

    // sort | uniq -c | sort -rn | head -n n
    // List.sort is stable, so equal counts keep the alphabetical order of the first sort.
    static List<Count> topN(List<String> lines, int n) {
        List<String> sorted = new ArrayList<>(lines);
        sorted.sort(null);
        List<Count> counts = uniqC(sorted);
        counts.sort((a, b) -> Integer.compare(b.n(), a.n()));
        return counts.subList(0, Math.min(n, counts.size()));
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
