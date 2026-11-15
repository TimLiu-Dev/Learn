import java.util.*;

public class Practice {
    @FunctionalInterface
    interface Rule {
        boolean check(String password);
    }

    // The lambda captures n; n is never reassigned, so it's effectively final
    static Rule minLength(int n) {
        return pw -> pw.length() >= n;
    }

    // chars() gives an IntStream, so the reference targets IntPredicate: no boxing
    static final Rule HAS_DIGIT = pw -> pw.chars().anyMatch(Character::isDigit);

    static List<String> failures(String password, Map<String, Rule> rules) {
        return rules.entrySet().stream()
                .filter(e -> !e.getValue().check(password))
                .map(Map.Entry::getKey)
                .toList();
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
