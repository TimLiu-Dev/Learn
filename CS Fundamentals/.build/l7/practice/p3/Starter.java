import java.util.*;

public class Practice {
    sealed interface AgeInput permits Valid, NotANumber, OutOfRange, Blank {}
    record Valid(int years) implements AgeInput {}
    record NotANumber(String text) implements AgeInput {}
    record OutOfRange(int years) implements AgeInput {}    // added last week
    record Blank() implements AgeInput {}

    static AgeInput parseAge(String text) {
        throw new UnsupportedOperationException("TODO: Blank, NotANumber, OutOfRange (outside 0..150) or Valid");
    }

    static String describe(AgeInput input) {
        return switch (input) {
            case Valid v -> "age " + v.years();
            case NotANumber(String text) -> "not a number: " + text;
            default -> "please enter your age";              // written before OutOfRange existed
        };
    }

    static List<Integer> validAges(List<String> texts) {
        throw new UnsupportedOperationException("TODO: parse each text, keep only the Valid ones");
    }

    // CHECKS
    public static void main(String[] args) {
        check("blank text is Blank", new Blank(), () -> parseAge("   "));
        check("a whole number is Valid, after trimming", new Valid(42), () -> parseAge(" 42 "));
        check("0 and 150 are both Valid", List.of(new Valid(0), new Valid(150)), () -> List.of(parseAge("0"), parseAge("150")));
        check("words are NotANumber, keeping the trimmed text", new NotANumber("forty"), () -> parseAge(" forty "));
        check("4.5 is not a whole number", new NotANumber("4.5"), () -> parseAge("4.5"));
        check("numbers outside 0 to 150 are OutOfRange", List.of(new OutOfRange(200), new OutOfRange(-1)), () -> List.of(parseAge("200"), parseAge("-1")));
        check("every case gets its own message",
              List.of("age 7", "not a number: x", "out of range: 200", "please enter your age"),
              () -> List.of(describe(new Valid(7)), describe(new NotANumber("x")), describe(new OutOfRange(200)), describe(new Blank())));
        check("validAges keeps only the valid ones, in order", List.of(30, 7), () -> validAges(List.of("30", "", "abc", "999", " 7")));
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
