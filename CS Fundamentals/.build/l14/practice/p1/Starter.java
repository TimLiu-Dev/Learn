import java.util.*;

public class Practice {
    @FunctionalInterface
    interface Rule {
        boolean check(String password);
    }

    // A rule that needs at least n characters
    static Rule minLength(int n) {
        throw new UnsupportedOperationException("TODO: return a lambda; it becomes a Rule");
    }

    // At least one digit, anywhere
    static final Rule HAS_DIGIT = pw -> {
        throw new UnsupportedOperationException("TODO: pw.chars().anyMatch(...) with a method reference");
    };

    // The names of the rules this password breaks, in the map's order
    static List<String> failures(String password, Map<String, Rule> rules) {
        throw new UnsupportedOperationException("TODO: stream the entries, keep the failing ones, map to keys");
    }

    // CHECKS
    public static void main(String[] args) throws Exception {
        check("minLength counts characters", List.of(false, true), () -> List.of(minLength(8).check("hunter2"), minLength(8).check("hunter22")));
        check("each minLength rule keeps its own limit", List.of(true, false), () -> List.of(minLength(3).check("abcd"), minLength(10).check("abcd")));
        check("hasDigit finds a digit anywhere", List.of(true, false, false), () -> List.of(HAS_DIGIT.check("abc7def"), HAS_DIGIT.check("abcdef"), HAS_DIGIT.check("")));
        check("failures lists the broken rules in order", List.of("length", "digit", "no spaces"), () -> failures("a b", rules()));
        check("only the digit rule fails", List.of("digit"), () -> failures("correcthorse", rules()));
        check("a good password breaks nothing", List.of(), () -> failures("correct7horse", rules()));
        check("no rules means no failures", List.of(), () -> failures("x", Map.of()));
    }

    static Map<String, Rule> rules() {
        Map<String, Rule> rules = new LinkedHashMap<>();   // keeps insertion order, like Kotlin's mapOf
        rules.put("length", minLength(8));
        rules.put("digit", HAS_DIGIT);
        rules.put("no spaces", pw -> !pw.contains(" "));
        return rules;
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
