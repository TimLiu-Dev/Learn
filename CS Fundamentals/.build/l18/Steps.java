public class Steps {
    // How many times does the loop run in the worst case (target not there)?
    static int steps(int n, int target) {
        int lo = 0, hi = n - 1, count = 0;
        while (lo <= hi) {
            count++;
            int mid = lo + (hi - lo) / 2;
            if (mid < target) lo = mid + 1; else hi = mid - 1;   // a[i] == i, target never equal
        }
        return count;
    }

    public static void main(String[] args) {
        for (int n : new int[] {1_000, 1_000_000, 1_000_000_000}) {
            System.out.printf("n = %,13d   steps = %d%n", n, steps(n, Integer.MAX_VALUE));
        }
    }
}
