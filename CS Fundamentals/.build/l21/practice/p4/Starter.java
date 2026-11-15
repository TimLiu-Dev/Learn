import java.util.*;

public class Practice {
    static List<String> schedule(Map<String, List<String>> needs) {
        throw new UnsupportedOperationException("TODO: Kahn's algorithm, alphabetically first ready task each time");
    }

    // CHECKS
    public static void main(String[] args) {
        check("ready tasks run in alphabetical order", List.of("a", "b", "c"), () -> schedule(Map.of("b", List.of(), "c", List.of(), "a", List.of())));
        check("a task unlocked later can jump the queue", List.of("b", "a", "z"), () -> schedule(Map.of("z", List.of(), "b", List.of(), "a", List.of("b"))));
        check("prerequisites always come first", List.of("compile", "resources", "classes", "compileTest", "jar", "test", "build"), () -> schedule(Map.of(
            "compile", List.of(), "resources", List.of(), "classes", List.of("compile", "resources"),
            "compileTest", List.of("classes"), "test", List.of("compileTest"),
            "jar", List.of("classes"), "build", List.of("jar", "test"))));
        check("a cycle gives null", null, () -> schedule(Map.of("a", List.of("c"), "b", List.of("a"), "c", List.of("b"), "d", List.of())));
        check("no tasks gives an empty schedule", List.of(), () -> schedule(Map.of()));
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
