import java.util.Objects;

public class Practice {
    sealed interface AgeResult permits Ok, NotANumber, Missing {}
    record Ok(int age) implements AgeResult {}
    record NotANumber(String text) implements AgeResult {}
    record Missing() implements AgeResult {}

    // A row looks like "name,age", e.g. "Ada,36".
    static AgeResult readAge(String row) {
        String[] fields = row.split(",");
        if (fields.length < 2 || fields[1].isBlank()) return new Missing();
        String text = fields[1].trim();
        try {
            return new Ok(Integer.parseInt(text));
        } catch (NumberFormatException e) {      // only the failure we expect
            return new NotANumber(text);
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("a good row gives the age", new Ok(36), () -> readAge("Ada,36"));
        check("spaces around the fields are ignored", new Ok(41), () -> readAge(" Grace , 41 "));
        check("a non-number age keeps the text", new NotANumber("abc"), () -> readAge("Ada,abc"));
        check("a row with no age field is Missing, not NotANumber", new Missing(), () -> readAge("Ada"));
        check("a blank age is Missing", new Missing(), () -> readAge("Ada, "));
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
