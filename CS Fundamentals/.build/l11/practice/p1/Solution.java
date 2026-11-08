import java.util.*;

public class Practice {
    static void expectEqual(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("expected: <" + expected + "> but was: <" + actual + ">");
        }
    }

    static List<String> runAll(Map<String, Runnable> tests) {
        List<String> report = new ArrayList<>();
        for (Map.Entry<String, Runnable> test : tests.entrySet()) {
            try {
                test.getValue().run();
                report.add("PASS " + test.getKey());
            } catch (AssertionError e) {      // an Error, not an Exception (L10)
                report.add("FAIL " + test.getKey() + ": " + e.getMessage());
            }
        }
        return report;
    }

    // CHECKS
    public static void main(String[] args) {
        check("equal values pass quietly", "quiet", () -> {
            expectEqual(4, 2 + 2);
            expectEqual(List.of(1, 2), List.of(1, 2));
            expectEqual(null, null);
            return "quiet";
        });
        check("a mismatch throws an AssertionError with expected first", "expected: <$12.05> but was: <$12.5>", () -> {
            try { expectEqual("$12.05", "$12.5"); } catch (AssertionError e) { return e.getMessage(); }
            return "nothing was thrown";
        });
        check("a passing test is reported as PASS", List.of("PASS adds"),
                () -> runAll(tests("adds", () -> expectEqual(4, 2 + 2))));
        check("a failing test is reported with its message", List.of("FAIL pads cents: expected: <$12.05> but was: <$12.5>"),
                () -> runAll(tests("pads cents", () -> expectEqual("$12.05", "$12.5"))));
        check("one failure does not stop the tests after it", List.of("PASS first", "FAIL second: expected: <1> but was: <2>", "PASS third"),
                () -> runAll(tests("first", () -> expectEqual(1, 1), "second", () -> expectEqual(1, 2), "third", () -> expectEqual("a", "a"))));
    }

    // Ordered maps of test name -> body, like a test class's methods.
    static Map<String, Runnable> tests(String n1, Runnable b1) {
        Map<String, Runnable> m = new LinkedHashMap<>();
        m.put(n1, b1);
        return m;
    }

    static Map<String, Runnable> tests(String n1, Runnable b1, String n2, Runnable b2, String n3, Runnable b3) {
        Map<String, Runnable> m = tests(n1, b1);
        m.put(n2, b2);
        m.put(n3, b3);
        return m;
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception | AssertionError e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
