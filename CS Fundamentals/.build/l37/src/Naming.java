import java.util.LinkedHashMap;
import java.util.Map;

public class Naming {
    // BEFORE: what is l? what does f switch on? what is r?
    static Map<String, Integer> calc(String l, boolean f) {
        Map<String, Integer> r = new LinkedHashMap<>();
        for (String w : l.split("[^\\p{L}]+")) {
            if (w.isEmpty()) continue;
            String k = f ? w.toLowerCase() : w;
            r.merge(k, 1, Integer::sum);
        }
        return r;
    }

    // AFTER: Java has no named arguments, so the flag becomes two methods.
    /**
     * Counts how often each word appears in {@code text}, treating "The" and "the" as one word.
     *
     * <p>A word is a run of letters; everything else separates words.
     * @param text the text to scan
     * @return word to count, in first-seen order
     */
    static Map<String, Integer> wordCountsIgnoringCase(String text) {
        return countWords(text, true);
    }

    /** Like {@link #wordCountsIgnoringCase} but "The" and "the" are different words. */
    static Map<String, Integer> wordCounts(String text) {
        return countWords(text, false);
    }

    private static Map<String, Integer> countWords(String text, boolean ignoreCase) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String word : text.split("[^\\p{L}]+")) {
            if (word.isEmpty()) continue;
            String key = ignoreCase ? word.toLowerCase() : word;
            counts.merge(key, 1, Integer::sum);
        }
        return counts;
    }

    public static void main(String[] args) {
        String text = "The cat sat on the mat. The mat was flat.";
        System.out.println(calc(text, true));                 // true... what?
        System.out.println(wordCountsIgnoringCase(text));     // says what it does
        System.out.println(calc(text, true).equals(wordCountsIgnoringCase(text)));
    }
}
