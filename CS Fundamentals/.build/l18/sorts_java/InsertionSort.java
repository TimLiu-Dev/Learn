public class InsertionSort {
    // Grow a sorted prefix a[0..i-1]; slide a[i] left into place.
    static void sort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && Less.less(key, a[j])) {   // shift bigger items one step right
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;                            // drop key into the gap
        }
    }
}
