public class Swapped {
    static String formatPrice(long cents) {
        return String.format("$%d.%d", cents / 100, cents % 100);   // forgot the zero-padding
    }

    static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected: <" + expected + "> but was: <" + actual + ">");
        }
    }

    public static void main(String[] args) {
        String result = formatPrice(1205);
        try {
            assertEquals(result, "$12.05");      // arguments in the wrong order
            System.out.println("PASS");
        } catch (AssertionError e) {
            System.out.println("FAIL " + e.getMessage());
        }
    }
}
