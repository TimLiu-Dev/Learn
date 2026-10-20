package ds;

import java.util.Comparator;

// "Smaller" means "comes out of the min-heap first": higher count, then alphabetical.
public record WordCount(String word, int count) implements Comparable<WordCount> {
    private static final Comparator<WordCount> ORDER =
        Comparator.comparingInt(WordCount::count).reversed().thenComparing(WordCount::word);

    @Override public int compareTo(WordCount other) { return ORDER.compare(this, other); }
}
