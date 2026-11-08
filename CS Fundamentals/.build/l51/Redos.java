import java.util.List;
import java.util.function.IntFunction;
import java.util.regex.Pattern;

public class Redos {
    // One evil input per pattern: a run of n letters that ends in a character that can't match.
    record Case(String label, IntFunction<Pattern> regex, IntFunction<String> input) {}

    static final List<Case> CASES = List.of(
        new Case("(\\w+\\s?)*", n -> Pattern.compile("(\\w+\\s?)*"), n -> "a".repeat(n) + "!"),
        new Case("(['\"])(\\w+\\s?)*\\1", n -> Pattern.compile("(['\"])(\\w+\\s?)*\\1"), n -> "\"" + "a".repeat(n) + "!"),
        new Case("(\\w+\\s?){1,30}", n -> Pattern.compile("(\\w+\\s?){1,30}"), n -> "a".repeat(n) + "!"),
        new Case("a?{n}a{n}", n -> Pattern.compile("a?".repeat(n) + "a".repeat(n)), n -> "a".repeat(n)));

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++)                                  // warm up the JIT (L16)
            for (Case c : CASES) c.regex().apply(12).matcher(c.input().apply(12)).matches();
        for (Case c : CASES) {
            System.out.printf("%-20s", c.label());
            for (int n = 16; n <= 28; n += 4) {
                Pattern re = c.regex().apply(n);
                String input = c.input().apply(n);
                long t0 = System.nanoTime();
                boolean result = re.matcher(input).matches();           // whole-string match
                long ms = (System.nanoTime() - t0) / 1_000_000;
                System.out.printf("  n=%d %s %6d ms", n, result ? "yes" : "no ", ms);
            }
            System.out.println();
        }
    }
}
