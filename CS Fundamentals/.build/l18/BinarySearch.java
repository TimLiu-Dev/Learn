public class BinarySearch {
    static boolean trace = false;

    static int binarySearch(int[] a, int target) {
        int lo = 0;
        int hi = a.length - 1;
        // Invariant: if target is anywhere in a, it is in a[lo..hi].
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (trace) System.out.println("  lo=" + lo + " mid=" + mid + " hi=" + hi + "  a[mid]=" + a[mid]);
            if (a[mid] < target) lo = mid + 1;       // target is right of mid
            else if (a[mid] > target) hi = mid - 1;  // target is left of mid
            else return mid;
        }
        return -1;                                   // lo > hi: the range is empty
    }

    static int binarySearchRec(int[] a, int target, int lo, int hi) {
        if (lo > hi) return -1;                      // base case: empty range
        int mid = lo + (hi - lo) / 2;
        if (a[mid] < target) return binarySearchRec(a, target, mid + 1, hi);
        if (a[mid] > target) return binarySearchRec(a, target, lo, mid - 1);
        return mid;
    }

    public static void main(String[] args) {
        int[] a = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31};
        trace = true;
        System.out.println("search 23:"); System.out.println("-> " + binarySearch(a, 23));
        System.out.println("search 4:");  System.out.println("-> " + binarySearch(a, 4));
        trace = false;
        System.out.println("recursive: " + binarySearchRec(a, 23, 0, a.length - 1)
                + " " + binarySearchRec(a, 4, 0, a.length - 1));
    }
}
