package katas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Sorting {
    private Sorting() {}

    // O(n²) worst case, O(n) on sorted input, stable, in place.
    public static <T extends Comparable<? super T>> void insertionSort(List<T> xs) {
        for (int i = 1; i < xs.size(); i++) {
            T key = xs.get(i);
            int j = i - 1;
            while (j >= 0 && key.compareTo(xs.get(j)) < 0) {
                xs.set(j + 1, xs.get(j));
                j--;
            }
            xs.set(j + 1, key);
        }
    }

    // O(n log n) always, stable, O(n) extra space (one aux buffer for the whole sort).
    public static <T extends Comparable<? super T>> void mergeSort(List<T> xs) {
        List<T> aux = new ArrayList<>(xs);
        mergeSort(xs, aux, 0, xs.size());
    }

    private static <T extends Comparable<? super T>> void mergeSort(List<T> xs, List<T> aux, int lo, int hi) {
        if (hi - lo < 2) return;                     // half-open [lo, hi): 0 or 1 items
        int mid = lo + (hi - lo) / 2;
        mergeSort(xs, aux, lo, mid);
        mergeSort(xs, aux, mid, hi);
        for (int k = lo; k < hi; k++) aux.set(k, xs.get(k));
        int i = lo, j = mid;
        for (int k = lo; k < hi; k++) {
            if (i >= mid) xs.set(k, aux.get(j++));                              // left half used up
            else if (j >= hi) xs.set(k, aux.get(i++));                          // right half used up
            else if (aux.get(j).compareTo(aux.get(i)) < 0) xs.set(k, aux.get(j++)); // strictly smaller: take right
            else xs.set(k, aux.get(i++));                                       // ties take left: stable
        }
    }

    // O(n log n) on average, not stable, in place.
    // Median-of-three pivot copes with sorted input; 3-way partition copes with duplicates.
    public static <T extends Comparable<? super T>> void quickSort(List<T> xs) {
        quickSort(xs, 0, xs.size() - 1);
    }

    private static <T extends Comparable<? super T>> void quickSort(List<T> xs, int lo, int hi) {
        if (lo >= hi) return;
        T pivot = medianOf(xs.get(lo), xs.get(lo + (hi - lo) / 2), xs.get(hi));
        int lt = lo;                                 // xs[lo..lt-1]  < pivot
        int i = lo;                                  // xs[lt..i-1]  == pivot
        int gt = hi;                                 // xs[gt+1..hi]  > pivot
        while (i <= gt) {
            int c = xs.get(i).compareTo(pivot);
            if (c < 0) Collections.swap(xs, lt++, i++);
            else if (c > 0) Collections.swap(xs, i, gt--);
            else i++;
        }
        quickSort(xs, lo, lt - 1);
        quickSort(xs, gt + 1, hi);
    }

    private static <T extends Comparable<? super T>> T medianOf(T a, T b, T c) {
        if (a.compareTo(b) < 0) {
            if (b.compareTo(c) < 0) return b;
            return a.compareTo(c) < 0 ? c : a;
        }
        if (a.compareTo(c) < 0) return a;
        return b.compareTo(c) < 0 ? c : b;
    }
}
