package katas;

import java.util.List;

public final class Searching {
    private Searching() {}

    // First index i with xs[i] >= target, or xs.size() if there is none.
    // xs must be sorted. O(log n).
    public static <T extends Comparable<? super T>> int lowerBound(List<T> xs, T target) {
        int lo = 0;
        int hi = xs.size();                          // half-open [lo, hi)
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;            // no (lo + hi) overflow
            if (xs.get(mid).compareTo(target) < 0) lo = mid + 1; else hi = mid;
        }
        return lo;
    }
}
