import java.text.BreakIterator;

public class Truncate {
    // Cut s to at most maxUnits UTF-16 units (say, a database column's limit)
    // without splitting a user-perceived character.
    static String truncate(String s, int maxUnits) {
        if (s.length() <= maxUnits) return s;
        BreakIterator boundaries = BreakIterator.getCharacterInstance();
        boundaries.setText(s);
        int end = boundaries.isBoundary(maxUnits) ? maxUnits : boundaries.preceding(maxUnits);
        return s.substring(0, end);
    }

    public static void main(String[] args) {
        String bio = "Hi 😀 from café";   // the é is e + combining accent
        System.out.println(bio.length());
        System.out.println(bio.substring(0, 4));      // cuts the emoji's surrogate pair in half
        System.out.println(truncate(bio, 4));
        System.out.println(bio.substring(0, 15));     // keeps the e, loses its accent
        System.out.println(truncate(bio, 15));
    }
}
