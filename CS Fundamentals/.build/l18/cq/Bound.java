public class Bound {
    static int search(int[] a, int target) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] <= target) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }

    public static void main(String[] args) {
        int[] a = {1, 2, 2, 2, 3, 5};
        System.out.println(search(a, 2) + " " + search(a, 4));
    }
}
