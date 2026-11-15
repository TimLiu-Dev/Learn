import java.util.*;

public class Practice {
    // Subset sum. A proposed answer (a certificate) is a list of item indices.

    // The verifier: one pass over the certificate.
    static boolean verify(int[] sizes, int target, List<Integer> pick) {
        if (new HashSet<>(pick).size() != pick.size()) return false;     // no item used twice
        int sum = 0;
        for (int i : pick) {
            if (i < 0 || i >= sizes.length) return false;                // check the range before indexing
            sum += sizes[i];
        }
        return sum == target;
    }

    // The searcher: up to 2^n subsets, each one checked by verify.
    static List<Integer> search(int[] sizes, int target) {
        for (int mask = 0; mask < (1 << sizes.length); mask++) {
            List<Integer> pick = new ArrayList<>();
            for (int i = 0; i < sizes.length; i++) if ((mask & (1 << i)) != 0) pick.add(i);
            if (verify(sizes, target, pick)) return pick;
        }
        return null;
    }

    // CHECKS
    static final int[] SIZES = {3, 34, 4, 12, 5, 2};

    public static void main(String[] args) {
        check("a correct certificate is accepted", true, () -> verify(SIZES, 9, List.of(2, 4)));
        check("a wrong sum is rejected", false, () -> verify(SIZES, 9, List.of(0, 1)));
        check("a certificate can't use the same item twice", false, () -> verify(SIZES, 6, List.of(0, 0)));
        check("an index outside the list is rejected, not thrown", "false false",
            () -> verify(SIZES, 9, List.of(6)) + " " + verify(SIZES, 9, List.of(-1, 2, 4)));
        check("search finds a subset that the verifier accepts", true, () -> {
            List<Integer> pick = search(SIZES, 9);
            return pick != null && verify(SIZES, 9, pick);
        });
        check("search returns null when no subset works", null, () -> search(new int[] {2, 4, 6, 8}, 7));
        check("the empty subset sums to 0", List.of(), () -> search(SIZES, 0));
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
