import java.util.*;
import java.util.function.IntFunction;

public class Practice {
    record Case(int score, String expected) {}

    // One row per test case: a score, and the grade the spec demands.
    // The spec: 90..100 "A", 80..89 "B", 70..79 "C", 0..69 "F", anything else "invalid".
    static List<Case> gradeCases() {
        return List.of(
            new Case(95, "A"),
            new Case(50, "F")
        );
    }

    // CHECKS
    // The real grade(), and buggy copies of it ("mutants"): valid range lo..hi, and where A, B and C start.
    static String grade(int score, int lo, int hi, int a, int b, int c) {
        if (score < lo || score > hi) return "invalid";
        if (score >= a) return "A";
        if (score >= b) return "B";
        if (score >= c) return "C";
        return "F";
    }

    static String grade(int score) { return grade(score, 0, 100, 90, 80, 70); }

    // Caught: some correct row gets a different answer from the mutant.
    static boolean caught(IntFunction<String> mutant) {
        for (Case row : gradeCases()) {
            if (row.expected().equals(grade(row.score())) && !mutant.apply(row.score()).equals(row.expected())) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        check("every row agrees with the spec", List.of(), () -> {
            List<String> wrong = new ArrayList<>();
            for (Case row : gradeCases()) if (!row.expected().equals(grade(row.score()))) wrong.add(row.toString());
            return wrong;
        });
        check("at most 10 rows", true, () -> gradeCases().size() <= 10);
        check("catches an A that starts at 91", true, () -> caught(s -> grade(s, 0, 100, 91, 80, 70)));
        check("catches an A that starts at 89", true, () -> caught(s -> grade(s, 0, 100, 89, 80, 70)));
        check("catches a B that starts at 81", true, () -> caught(s -> grade(s, 0, 100, 90, 81, 70)));
        check("catches a C that starts at 71", true, () -> caught(s -> grade(s, 0, 100, 90, 80, 71)));
        check("catches a validator that rejects 0", true, () -> caught(s -> grade(s, 1, 100, 90, 80, 70)));
        check("catches a validator that rejects 100", true, () -> caught(s -> grade(s, 0, 99, 90, 80, 70)));
        check("catches a validator that accepts 101", true, () -> caught(s -> grade(s, 0, 101, 90, 80, 70)));
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
