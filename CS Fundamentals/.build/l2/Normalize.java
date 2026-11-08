import java.text.BreakIterator;
import java.text.Normalizer;
import java.util.List;

public class Normalize {
    // Count user-perceived characters: grapheme clusters (UAX #29)
    static int graphemes(String s) {
        BreakIterator boundaries = BreakIterator.getCharacterInstance();
        boundaries.setText(s);
        int count = 0;
        while (boundaries.next() != BreakIterator.DONE) count++;
        return count;
    }

    public static void main(String[] args) {
        String composed = "café";       // é as ONE code point, U+00E9
        String decomposed = "café";    // e + U+0301 COMBINING ACUTE ACCENT
        System.out.println(composed + " " + decomposed);   // they look the same...
        System.out.println(composed.equals(decomposed));   // ...but the chars differ
        System.out.println(composed.length() + " " + decomposed.length());

        String nfc = Normalizer.normalize(decomposed, Normalizer.Form.NFC);
        System.out.println(composed.equals(nfc));          // equal once both are in NFC
        String nfd = Normalizer.normalize(composed, Normalizer.Form.NFD);
        System.out.println(nfd.equals(decomposed));

        String family = "👨‍👩‍👧";   // man, ZWJ, woman, ZWJ, girl
        for (String s : List.of(decomposed, family)) {
            System.out.println(s + " length=" + s.length() + " codePoints=" + s.codePointCount(0, s.length())
                    + " graphemes=" + graphemes(s));
        }
    }
}
