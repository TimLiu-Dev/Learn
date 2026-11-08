import java.util.*;

public class Practice {
    // 1. An assertion: quiet when equal, loud when not.
    static void expectEqual(Object expected, Object actual) {
        throw new UnsupportedOperationException("TODO: compare with Objects.equals, throw an AssertionError on a mismatch");
    }

    // 2. A runner: body.run() runs one test. Report every test, even after a failure.
    static List<String> runAll(Map<String, Runnable> tests) {
        throw new UnsupportedOperationException("TODO: run each body inside try/catch and build the report lines");
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
