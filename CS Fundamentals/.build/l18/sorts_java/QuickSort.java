public class QuickSort {
    static void sort(int[] a) { sort(a, 0, a.length - 1); }

    // Pick a pivot, partition: smaller items left of it, the rest right. Recurse on both sides.
    static void sort(int[] a, int lo, int hi) {
        if (lo >= hi) return;
        int p = partition(a, lo, hi);                  // a[p] is now in its final place
        sort(a, lo, p - 1);
        sort(a, p + 1, hi);
    }

    // Lomuto partition with the naive choice: the LAST element is the pivot.
    static int partition(int[] a, int lo, int hi) {
        int pivot = a[hi];
        int i = lo;                                    // a[lo..i-1] < pivot
        for (int j = lo; j < hi; j++) {
            if (Less.less(a[j], pivot)) { Less.swap(a, i, j); i++; }
        }
        Less.swap(a, i, hi);                           // pivot goes between the two parts
        return i;
    }
}
