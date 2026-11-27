package search;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Turns text into index terms, exactly as the spec says. Used for documents and queries alike. */
public final class Tokenizer {
    private Tokenizer() {}

    /** The 50 stop words from the spec. They are never indexed and never searched for. */
    public static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "but", "by", "for",
            "from", "had", "has", "have", "he", "her", "him", "his", "i", "in",
            "is", "it", "its", "me", "my", "of", "on", "or", "she", "so",
            "that", "the", "their", "them", "then", "there", "they", "this", "to", "was",
            "we", "were", "what", "when", "which", "who", "will", "with", "you", "your");

    /**
     * Splits on code points (not chars), so a letter outside the BMP such as U+1D400
     * counts as one letter, not as two lone surrogates (L2).
     */
    public static List<String> tokenize(String text) {
        int[] cps = text.replace('’', '\'').codePoints().toArray();
        List<String> out = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < cps.length; i++) {
            int c = cps[i];
            if (Character.isLetterOrDigit(c)) {
                current.appendCodePoint(c);
            } else if (c == '\'' && !current.isEmpty()
                    && i + 1 < cps.length && Character.isLetterOrDigit(cps[i + 1])) {
                // A non-empty token means the previous code point was a letter or digit.
                current.append('\'');
            } else {
                flush(current, out);
            }
        }
        flush(current, out);
        return out;
    }

    private static void flush(StringBuilder current, List<String> out) {
        if (current.isEmpty()) return;
        String token = current.toString().toLowerCase(Locale.ROOT);
        if (!STOP_WORDS.contains(token)) out.add(token);
        current.setLength(0);
    }
}
