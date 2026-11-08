import java.text.BreakIterator;
import java.text.Normalizer;
import java.util.Objects;

public class Practice {
    static boolean sameText(String a, String b) {
        return Normalizer.normalize(a, Normalizer.Form.NFC).equals(Normalizer.normalize(b, Normalizer.Form.NFC));
    }

    static String preview(String s, int max) {
        BreakIterator boundaries = BreakIterator.getCharacterInstance();
        boundaries.setText(s);
        // The boundary after character number max, or DONE if the text runs out first
        int end = boundaries.next(max);
        return (end == BreakIterator.DONE || end == s.length()) ? s : s.substring(0, end) + "…";
    }

    // CHECKS
    public static void main(String[] args) {
        check("composed and decomposed e-acute are the same text", true, () -> sameText("café", "café"));
        check("different text stays different", false, () -> sameText("cafe", "café"));
        check("text that fits comes back unchanged", "Ada", () -> preview("Ada", 5));
        check("exactly max characters: no ellipsis", "Ada", () -> preview("Ada", 3));
        check("long text is cut and marked", "Hello…", () -> preview("Hello world", 5));
        check("an emoji counts as one character", "😀😀…", () -> preview("😀😀😀", 2));
        check("an accent stays with its letter", "café…", () -> preview("café au lait", 4));
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
