import java.util.Objects;

public class Practice {
    static class Fraction {
        final int num;
        final int den;

        Fraction(int numerator, int denominator) {
            // Check first: gcd(0, 0) is 0, and dividing by it would throw ArithmeticException.
            if (denominator == 0) throw new IllegalArgumentException("denominator must not be 0");
            int g = gcd(numerator, denominator);
            int sign = denominator < 0 ? -1 : 1;
            num = sign * numerator / g;
            den = sign * denominator / g;
        }

        Fraction(int whole) {
            this(whole, 1);
        }

        Fraction(String text) {
            this(part(text, 0), part(text, 1));    // this(...) must come first, so parse inside it
        }

        private static int part(String text, int i) {
            return Integer.parseInt(text.split("/")[i].trim());
        }
    }

    // Greatest common divisor, always >= 0. gcd(6, 8) == 2, gcd(0, 5) == 5.
    static int gcd(int a, int b) { return b == 0 ? Math.abs(a) : gcd(b, a % b); }

    // CHECKS
    static String show(Fraction f) { return f.num + "/" + f.den; }

    static String thrown(Check c) {
        try { c.get(); return "nothing"; } catch (Exception e) { return e.getClass().getSimpleName(); }
    }

    public static void main(String[] args) {
        check("a fraction is stored in lowest terms", "3/4 0/1", () -> show(new Fraction(6, 8)) + " " + show(new Fraction(0, 5)));
        check("the denominator defaults to 1", "5/1", () -> show(new Fraction(5)));
        check("the sign always lives in the numerator", "-1/2 2/3", () -> show(new Fraction(1, -2)) + " " + show(new Fraction(-4, -6)));
        check("a zero denominator is rejected", "IllegalArgumentException", () -> thrown(() -> new Fraction(3, 0)));
        check("zero over zero is rejected before any arithmetic", "IllegalArgumentException", () -> thrown(() -> new Fraction(0, 0)));
        check("the text constructor parses and simplifies", "1/2 -3/4", () -> show(new Fraction("4/8")) + " " + show(new Fraction("3/-4")));
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
