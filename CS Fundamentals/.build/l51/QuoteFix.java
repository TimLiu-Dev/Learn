import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class QuoteFix {
    // A quoted phrase: the same quote at both ends, words separated by single spaces.
    static final Pattern EVIL = Pattern.compile("(['\"])(\\w+\\s?)*\\1");
    static final Pattern POSSESSIVE = Pattern.compile("(['\"])(\\w+\\s?)*+\\1");      // *+ : never give back
    static final Pattern UNAMBIGUOUS = Pattern.compile("(['\"])(\\w+(\\s\\w+)*\\s?)?\\1"); // one way to split

    public static void main(String[] args) {
        // 1. Same language? Compare on every string of length <= 7 over a small alphabet.
        String alphabet = "a \"'!";
        List<String> all = List.of("");
        int checked = 0;
        for (int len = 1; len <= 7; len++) {
            List<String> longer = new ArrayList<>();
            for (String p : all) for (char c : alphabet.toCharArray()) longer.add(p + c);
            all = longer;
            for (String s : all) {
                checked++;
                boolean e = EVIL.matcher(s).matches();
                if (e != POSSESSIVE.matcher(s).matches() || e != UNAMBIGUOUS.matcher(s).matches())
                    throw new IllegalStateException("differ on [" + s + "]");
            }
        }
        System.out.println("all three agree on " + checked + " strings");
        // 2. Same speed? The evil input: an opening quote, n letters, no closing quote.
        List<Pattern> all3 = List.of(EVIL, POSSESSIVE, UNAMBIGUOUS);
        for (int i = 0; i < 3; i++) for (Pattern p : all3) p.matcher("\"" + "a".repeat(12) + "!").matches();
        for (Pattern p : all3) {
            System.out.printf("%-28s", p.pattern());
            for (int n : new int[] {20, 24, 28}) {
                long t0 = System.nanoTime();
                p.matcher("\"" + "a".repeat(n) + "!").matches();
                System.out.printf("  n=%d %6d ms", n, (System.nanoTime() - t0) / 1_000_000);
            }
            System.out.println();
        }
    }
}
