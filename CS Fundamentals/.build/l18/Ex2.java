public class Ex2 {
    // First index i with a[i] >= target, or a.length if there is none.
    static int lowerBound(int[] a, int target) {
        int lo = 0, hi = a.length;      // half-open range [lo, hi): hi is a valid answer
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] < target) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    // First index i with a[i] > target, or a.length if there is none.
    static int upperBound(int[] a, int target) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] <= target) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    static int countOf(int[] a, int x) {
        return upperBound(a, x) - lowerBound(a, x);
    }

    public static void main(String[] args) {
        int[] a = {1, 2, 2, 2, 3, 5, 8};
        System.out.println("lower(2)=" + lowerBound(a, 2) + " upper(2)=" + upperBound(a, 2) + " count(2)=" + countOf(a, 2));
        System.out.println("lower(4)=" + lowerBound(a, 4) + " count(4)=" + countOf(a, 4));
        System.out.println("lower(0)=" + lowerBound(a, 0) + " lower(9)=" + lowerBound(a, 9));
        System.out.println("empty: " + lowerBound(new int[0], 7));
    }
}
