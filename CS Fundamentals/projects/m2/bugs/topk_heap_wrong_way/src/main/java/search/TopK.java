package search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/** The k best items of a stream, without sorting all of it: O(n log k) time, O(k) memory (L20). */
public final class TopK {
    private TopK() {}

    /**
     * Returns at most {@code k} items, best first. {@code bestFirst} orders the items the way
     * they should be printed: a negative result means the first argument ranks higher.
     */
    public static <T> List<T> select(Iterable<T> items, int k, Comparator<T> bestFirst) {
        if (k < 1) throw new IllegalArgumentException("k must be at least 1, was " + k);
        // A min-heap under the ranking: its root is the weakest of the current top k,
        // the first one to evict when something better arrives.
        PriorityQueue<T> heap = new PriorityQueue<>(bestFirst); // BUG: root is the BEST, so it evicts the best
        for (T item : items) {
            heap.add(item);
            if (heap.size() > k) heap.poll();
        }
        List<T> result = new ArrayList<>(heap.size());
        while (!heap.isEmpty()) result.add(heap.poll());
        return result.reversed();
    }
}
