import java.util.*;

public class Practice {
    // add("1,2") is 3. The checks, in order, tell you the rest.
    static int add(String numbers) {
        throw new UnsupportedOperationException("TODO: make step 1 pass, run, then step 2...");
    }

    // CHECKS
    public static void main(String[] args) {
        check("step 1 - the empty string is 0", 0, () -> add(""));
        check("step 2 - one number is itself", 17, () -> add("17"));
        check("step 3 - two numbers are added", 3, () -> add("1,2"));
        check("step 4 - any count of numbers is added", 150, () -> add("10,20,30,40,50"));
        check("step 5 - a newline also separates numbers", 6, () -> add("1\n2,3"));
        check("step 6 - negatives are rejected, and the message lists them all", "negatives not allowed: -2, -4", () -> {
            try { add("1,-2,3,-4"); } catch (IllegalArgumentException e) { return e.getMessage(); }
            return "nothing was thrown";
        });
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
