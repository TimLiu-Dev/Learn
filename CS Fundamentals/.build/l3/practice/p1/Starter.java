import java.util.Objects;

public class Practice {
    static int ticketPrice(int age) {
        throw new UnsupportedOperationException("TODO: an if/else chain (switch can't match ranges)");
    }

    static String fizzBuzz(int i) {
        throw new UnsupportedOperationException("TODO: Fizz, Buzz, FizzBuzz or the number as text");
    }

    static String fizzBuzzLine(int n) {
        throw new UnsupportedOperationException("TODO: loop from 1 to n, with a space between answers");
    }

    // CHECKS
    public static void main(String[] args) {
        check("age 2 is free", 0, () -> ticketPrice(2));
        check("age 3 pays 5", 5, () -> ticketPrice(3));
        check("age 12 pays 5", 5, () -> ticketPrice(12));
        check("age 13 pays 12", 12, () -> ticketPrice(13));
        check("age 64 pays 12", 12, () -> ticketPrice(64));
        check("age 65 pays 6", 6, () -> ticketPrice(65));
        check("age 0 is free", 0, () -> ticketPrice(0));
        check("a negative age is invalid", -1, () -> ticketPrice(-1));
        check("3 says Fizz", "Fizz", () -> fizzBuzz(3));
        check("5 says Buzz", "Buzz", () -> fizzBuzz(5));
        check("7 says 7", "7", () -> fizzBuzz(7));
        check("15 says FizzBuzz", "FizzBuzz", () -> fizzBuzz(15));
        check("the line for 5", "1 2 Fizz 4 Buzz", () -> fizzBuzzLine(5));
        check("the line for 0 is empty", "", () -> fizzBuzzLine(0));
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
