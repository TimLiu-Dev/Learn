import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Practice {
    // Which items changed between two snapshots? A missing key counts as null.
    static List<String> changedKeys(Map<String, Integer> before, Map<String, Integer> after) {
        Set<String> keys = new TreeSet<>(before.keySet());   // TreeSet keeps the keys sorted
        keys.addAll(after.keySet());
        List<String> changed = new ArrayList<>();
        for (String k : keys) {
            if (before.get(k) != after.get(k)) changed.add(k);
        }
        return changed;
    }

    // CHECKS
    public static void main(String[] args) {
        check("small unchanged counts are not reported", List.of(), () -> changedKeys(map("nuts", 12), map("nuts", 12)));
        check("big unchanged counts are not reported", List.of(), () -> changedKeys(map("bolts", 500), map("bolts", 500)));
        check("real changes are reported, sorted", List.of("gears", "nuts"),
                () -> changedKeys(map("nuts", 12, "bolts", 500, "gears", 1000), map("nuts", 13, "bolts", 500, "gears", 999)));
        check("a count that became unknown is a change", List.of("bolts"), () -> changedKeys(map("bolts", 500), map("bolts", null)));
        check("two unknown counts are not a change", List.of(), () -> changedKeys(map("bolts", null), map("bolts", null)));
        check("added and removed items are changes", List.of("gears", "nuts"), () -> changedKeys(map("nuts", 5), map("gears", 5)));
    }

    // Map.of refuses null values, so the checks build HashMaps: map("a", 1, "b", null).
    static Map<String, Integer> map(Object... keysAndValues) {
        Map<String, Integer> m = new HashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) m.put((String) keysAndValues[i], (Integer) keysAndValues[i + 1]);
        return m;
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
