import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class Practice {
    // Visiting order of an n x n grid stored flat: cell (r, c) is at index r * n + c.
    static List<Integer> rowMajor(int n) {
        List<Integer> out = new ArrayList<>();
        for (int r = 0; r < n; r++)
            for (int c = 0; c < n; c++) out.add(r * n + c);
        return out;
    }

    static List<Integer> columnMajor(int n) {
        throw new UnsupportedOperationException("TODO: outer loop over columns, inner loop over rows");
    }

    // Replay the accesses through an LRU cache holding `lines` cache lines of `intsPerLine` ints.
    static int misses(List<Integer> indices, int intsPerLine, int lines) {
        throw new UnsupportedOperationException("TODO: work out each index's line; a line not in the cache is a miss");
    }

    // CHECKS
    public static void main(String[] args) {
        check("column-major visits down each column first", List.of(0, 3, 6, 1, 4, 7, 2, 5, 8), () -> columnMajor(3));
        check("row-major on the stepper's grid misses once per line", 16, () -> misses(rowMajor(8), 4, 4));
        check("column-major on the stepper's grid misses every time", 64, () -> misses(columnMajor(8), 4, 4));
        check("a cache big enough for the whole grid rescues column-major", 16, () -> misses(columnMajor(8), 4, 16));
        check("a hit makes a line recently used", 3, () -> misses(List.of(0, 4, 0, 8, 0), 4, 2));
        check("no accesses, no misses", 0, () -> misses(List.of(), 4, 4));
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
