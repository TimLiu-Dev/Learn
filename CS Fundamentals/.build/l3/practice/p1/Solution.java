import java.util.Objects;

public class Practice {
    static int ticketPrice(int age) {
        if (age < 0) return -1;
        else if (age <= 2) return 0;
        else if (age <= 12) return 5;
        else if (age <= 64) return 12;
        else return 6;
    }

    static String fizzBuzz(int i) {
        if (i % 15 == 0) return "FizzBuzz";      // first: the first true test wins
        else if (i % 3 == 0) return "Fizz";
        else if (i % 5 == 0) return "Buzz";
        else return String.valueOf(i);
    }

    static String fizzBuzzLine(int n) {
        String line = "";
        for (int i = 1; i <= n; i++) {
            if (i > 1) line += " ";
            line += fizzBuzz(i);
        }
        return line;
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
