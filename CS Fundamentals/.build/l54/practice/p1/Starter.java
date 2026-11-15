import java.util.*;

public class Practice {
    static class CalcError extends RuntimeException {
        CalcError(String message) { super(message); }
    }

    static List<String> tokenize(String src) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < src.length()) {
            char c = src.charAt(i);
            throw new UnsupportedOperationException("TODO: skip a space, scan a number, scan ** or a one-character operator, or throw CalcError");
        }
        return tokens;
    }

    // CHECKS
    static Object errorOf(String src) {
        try { return "no error: " + tokenize(src); } catch (CalcError e) { return e.getMessage(); }
    }

    public static void main(String[] args) {
        check("numbers can have many digits", List.of("12", "+", "345"), () -> tokenize("12+345"));
        check("spaces never become tokens", List.of("1", "*", "(", "2", "-", "30", ")"), () -> tokenize("  1 *( 2-  30 ) "));
        check("only spaces gives no tokens", List.of(), () -> tokenize("   "));
        check("a space splits a number in two", List.of("1", "2"), () -> tokenize("1 2"));
        check("the longest operator wins", List.of("2", "**", "3", "*", "4"), () -> tokenize("2**3*4"));
        check("three stars are ** then *", List.of("2", "**", "*", "3"), () -> tokenize("2***3"));
        check("an unknown character is reported with its column", "unexpected '#' at column 5", () -> errorOf("1 + #"));
        check("the error is found even after a long number", "unexpected 'x' at column 4", () -> errorOf("123x"));
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
