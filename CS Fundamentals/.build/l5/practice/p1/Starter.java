import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    // Each method compiles, but asks the wrong question. Fix the comparison in each one.

    // Do the two score sheets hold the same numbers, in the same order?
    static boolean sameScores(int[] a, int[] b) { return a.equals(b); }

    // Would a change made through a show up through b?
    static boolean isSameList(List<Integer> a, List<Integer> b) { return a.equals(b); }

    // Where is the name the user typed? -1 if it isn't there.
    static int findName(List<String> names, String typed) {
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i) == typed) return i;
        }
        return -1;
    }

    // CHECKS
    public static void main(String[] args) {
        check("equal score sheets match", true, () -> sameScores(new int[]{90, 75}, new int[]{90, 75}));
        check("reordered score sheets don't match", false, () -> sameScores(new int[]{90, 75}, new int[]{75, 90}));
        check("shorter score sheets don't match", false, () -> sameScores(new int[]{90}, new int[]{90, 75}));
        check("an alias is the same list", true, () -> { List<Integer> a = new ArrayList<>(List.of(1, 2)); List<Integer> b = a; return isSameList(a, b); });
        check("an equal copy is not the same list", false, () -> { List<Integer> a = new ArrayList<>(List.of(1, 2)); return isSameList(a, new ArrayList<>(a)); });
        check("a typed name is found by its contents", 1, () -> findName(List.of("Ada", "Linus", "Grace"), new StringBuilder("Lin").append("us").toString()));
        check("a missing name gives -1", -1, () -> findName(List.of("Ada", "Grace"), "Alan"));
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
