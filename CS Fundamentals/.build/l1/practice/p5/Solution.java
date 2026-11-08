import java.util.Objects;

public class Practice {
    static String greeting(String name) {
        return "Hello, " + name + "!";   // Java has no string templates, so join the pieces with +
    }

    // CHECKS
    public static void main(String[] args) {
        check("greets the katas folder", "Hello, katas!", () -> greeting("katas"));
        check("uses the name it is given", "Hello, Ada!", () -> greeting("Ada"));
        check("keeps a name with a space in one piece", "Hello, Grace Hopper!", () -> greeting("Grace Hopper"));
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
