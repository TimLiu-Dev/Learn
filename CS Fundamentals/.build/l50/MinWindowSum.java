// Shortest contiguous run of POSITIVE numbers whose sum is at least target; 0 if none.
public class MinWindowSum {
    static int shortestAtLeast(int[] a, int target) {
        for (int x : a) if (x <= 0) throw new IllegalArgumentException("all numbers must be positive");
        int left = 0, sum = 0, best = Integer.MAX_VALUE;
        for (int right = 0; right < a.length; right++) {
            sum += a[right];                             // grow on the right
            while (sum >= target) {                      // valid: record it, then try a shorter one
                best = Math.min(best, right - left + 1);
                sum -= a[left++];
            }
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }

    public static void main(String[] args) {
        System.out.println(shortestAtLeast(new int[] {2, 3, 1, 2, 4, 3}, 7));   // [4, 3]
        System.out.println(shortestAtLeast(new int[] {1, 4, 4}, 4));            // [4]
        System.out.println(shortestAtLeast(new int[] {1, 1, 1}, 11));           // impossible
    }
}
