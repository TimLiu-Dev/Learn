import java.util.Objects;

public class Practice {
    static String initials(String... names) {
        throw new UnsupportedOperationException("TODO: delegate to initialsWith with the default suffix");
    }

    // A varargs parameter must come last, so the suffix goes first.
    static String initialsWith(String suffix, String... names) {
        throw new UnsupportedOperationException("TODO: each first letter, uppercased, then suffix");
    }

    static int clamp(int value) {
        throw new UnsupportedOperationException("TODO: delegate with the defaults 0 and 100");
    }

    static int clamp(int value, int min, int max) {
        throw new UnsupportedOperationException("TODO: keep value inside min..max");
    }

    // CHECKS
    public static void main(String[] args) {
        check("every initial is followed by the suffix", "A.L.", () -> initials("Ada", "Lovelace"));
        check("initials are uppercased", "G.B.H.", () -> initials("grace", "brewster", "hopper"));
        check("an explicit suffix replaces the dot", "AT", () -> initialsWith("", "alan", "turing"));
        check("no names give nothing", "", () -> initials());
        check("an array can be passed to the varargs", "L.B.T.", () -> initials(new String[] {"linus", "benedict", "torvalds"}));
        check("clamp: 150 is above the default max", 100, () -> clamp(150));
        check("clamp: -5 is below the default min", 0, () -> clamp(-5));
        check("clamp: 42 is inside", 42, () -> clamp(42));
        check("clamp with max 40", 40, () -> clamp(50, 0, 40));
        check("clamp with min 10", 10, () -> clamp(5, 10, 100));
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
