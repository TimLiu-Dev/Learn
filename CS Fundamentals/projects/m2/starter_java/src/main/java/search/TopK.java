package search;

import java.util.Comparator;
import java.util.List;

/** The k best items of a stream, without sorting all of it (L20). Used by doc, q, or, prefix and top. */
public final class TopK {
    private TopK() {}

    /** At most k items, best first. bestFirst.compare(a, b) < 0 means a ranks higher. */
    public static <T> List<T> select(Iterable<T> items, int k, Comparator<T> bestFirst) {
        throw new UnsupportedOperationException("TODO: TopK.select (milestone 1): a heap of at most k items");
    }
}
