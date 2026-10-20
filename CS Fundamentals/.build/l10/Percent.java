import java.util.List;
import java.util.OptionalInt;

public class Percent {
    static int percentOrThrow(String text) {
        int n;
        try {
            n = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("not a number: '" + text + "'", e);
        }
        if (n < 0 || n > 100) throw new IllegalArgumentException("not a percentage: " + n);
        return n;
    }

    static OptionalInt percentOrEmpty(String text) {
        try {
            int n = Integer.parseInt(text);
            return (n >= 0 && n <= 100) ? OptionalInt.of(n) : OptionalInt.empty();
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    sealed interface PercentResult permits Ok, NotANumber, OutOfRange {}
    record Ok(int value) implements PercentResult {}
    record NotANumber(String input) implements PercentResult {}
    record OutOfRange(int value) implements PercentResult {}

    static PercentResult parsePercent(String text) {
        int n;
        try {
            n = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return new NotANumber(text);
        }
        return (n >= 0 && n <= 100) ? new Ok(n) : new OutOfRange(n);
    }

    public static void main(String[] args) {
        for (String input : List.of("42", "abc", "150")) {
            String a;
            try { a = String.valueOf(percentOrThrow(input)); } catch (IllegalArgumentException e) { a = "threw (" + e.getMessage() + ")"; }
            OptionalInt o = percentOrEmpty(input);
            String b = o.isPresent() ? String.valueOf(o.getAsInt()) : "empty";
            String c = switch (parsePercent(input)) {
                case Ok ok -> ok.value() + "%";
                case NotANumber n -> "not a number";
                case OutOfRange r -> r.value() + " is out of range";
            };
            System.out.println(input + ": " + a + " | " + b + " | " + c);
        }
    }
}
