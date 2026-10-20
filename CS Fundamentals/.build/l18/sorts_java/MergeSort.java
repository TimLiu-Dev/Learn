import java.util.Arrays;

public class MergeSort {
    // Split in half, sort each half recursively, merge the two sorted halves.
    static void sort(int[] a) {
        if (a.length < 2) return;                      // base case: 0 or 1 items are sorted
        int[] left = Arrays.copyOfRange(a, 0, a.length / 2);   // O(n) extra space
        int[] right = Arrays.copyOfRange(a, a.length / 2, a.length);
        sort(left);
        sort(right);
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            // take from right only if strictly smaller: ties come from left (stable)
            a[k++] = Less.less(right[j], left[i]) ? right[j++] : left[i++];
        }
        while (i < left.length) a[k++] = left[i++];    // copy whatever is left over
        while (j < right.length) a[k++] = right[j++];
    }
}
