import java.util.*;

public class Practice {
    static int firstError(String text) {
        throw new UnsupportedOperationException("TODO: return -1, or the index of the culprit");
    }

    // CHECKS
    public static void main(String[] args) {
        check("balanced text gives -1", List.of(-1, -1, -1, -1), () -> List.of(firstError(""), firstError("no brackets"), firstError("{[()]}"), firstError("f(a[0], {b})")));
        check("a closer with nothing open is the culprit", List.of(0, 2), () -> List.of(firstError(")"), firstError("())")));
        check("a closer of the wrong kind is the culprit", List.of(1, 2), () -> List.of(firstError("(]"), firstError("([)]")));
        check("a single opener left over is the culprit", List.of(0, 1), () -> List.of(firstError("("), firstError("a(b[c]")));
        check("with several left over, the leftmost is the culprit", List.of(0, 1), () -> List.of(firstError("(("), firstError("x{[()")));
        check("a bad closer is reported before leftovers", List.of(2, 1), () -> List.of(firstError("((]"), firstError("x)(")));
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
