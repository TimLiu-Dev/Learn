// Every comparison goes through less(), so we can count them (Sedgewick's trick).
public class Less {
    static long compares = 0;

    static boolean less(int a, int b) {
        compares++;
        return a < b;
    }

    static void swap(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }
}
