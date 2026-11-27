package search;

import java.util.List;
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

    public static List<String> tokenize(String text) {
        // Milestone 1: split on anything that isn't a letter or digit, lower-case, drop stop words.
        // Milestone 2: apostrophes, ’ (U+2019), and code points instead of chars.
        throw new UnsupportedOperationException("TODO: Tokenizer.tokenize (milestone 1)");
    }
}
