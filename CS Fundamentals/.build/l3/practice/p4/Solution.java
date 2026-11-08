import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Practice {
    static String firstRepeat(List<String> words) {
        Set<String> seen = new HashSet<>();
        for (String word : words) {
            if (!seen.add(word)) return word;    // add returns false if it was already there
        }
        return null;
    }

    static List<String> lowStock(Map<String, Integer> stock, int threshold) {
        List<String> low = new ArrayList<>();
        for (Map.Entry<String, Integer> e : stock.entrySet()) {
            if (e.getValue() < threshold) low.add(e.getKey());
        }
        Collections.sort(low);                   // Map.of has no fixed order, so sort
        return low;
    }

    // CHECKS
    public static void main(String[] args) {
        check("the first word seen twice is returned", "to", () -> firstRepeat(List.of("to", "be", "or", "not", "to", "be")));
        check("the word whose second copy comes first wins", "b", () -> firstRepeat(List.of("a", "b", "b", "a")));
        check("no repeat gives null", null, () -> firstRepeat(List.of("x", "y", "z")));
        check("an empty list gives null", null, () -> firstRepeat(List.of()));
        check("low stock in alphabetical order", List.of("apple", "fig", "pear"),
                () -> lowStock(Map.of("pear", 1, "kiwi", 9, "apple", 0, "fig", 2), 3));
        check("exactly the threshold is not low", List.of(), () -> lowStock(Map.of("plum", 3), 3));
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
