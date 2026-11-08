import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;

// Maximum of every window of size k, in O(n) total, with a monotonic deque of INDEXES.
// Invariant: values at the indexes in the deque are strictly decreasing from front to back,
// so the front is always the current window's maximum.
public class WindowMax {
    static int[] windowMax(int[] a, int k, boolean trace) {
        if (k < 1 || k > a.length) throw new IllegalArgumentException();
        int[] out = new int[a.length - k + 1];
        Deque<Integer> dq = new ArrayDeque<>();
        for (int i = 0; i < a.length; i++) {
            if (!dq.isEmpty() && dq.peekFirst() <= i - k) dq.pollFirst();       // front slid out of the window
            while (!dq.isEmpty() && a[dq.peekLast()] <= a[i]) dq.pollLast();    // smaller ones can never be a max again
            dq.addLast(i);
            if (i >= k - 1) out[i - k + 1] = a[dq.peekFirst()];
            if (trace) System.out.println("  i=" + i + " a=" + a[i] + "  deque values="
                    + dq.stream().map(j -> a[j]).toList() + (i >= k - 1 ? "  max=" + a[dq.peekFirst()] : ""));
        }
        return out;
    }

    static int[] windowMaxBrute(int[] a, int k) {
        int[] out = new int[a.length - k + 1];
        for (int s = 0; s < out.length; s++) {
            int m = Integer.MIN_VALUE;
            for (int j = s; j < s + k; j++) m = Math.max(m, a[j]);
            out[s] = m;
        }
        return out;
    }

    public static void main(String[] args) {
        int[] a = {1, 3, -1, -3, 5, 3, 6, 7};
        System.out.println("k=3:");
        System.out.println("-> " + Arrays.toString(windowMax(a, 3, true)));

        // Differential test: compare against the obvious O(n*k) version on 10,000 random inputs.
        Random rnd = new Random(50);
        for (int t = 0; t < 10_000; t++) {
            int n = rnd.nextInt(1, 30);
            int[] x = new int[n];
            for (int i = 0; i < n; i++) x[i] = rnd.nextInt(-5, 6);         // small range: lots of ties
            int k = rnd.nextInt(1, n + 1);
            if (!Arrays.equals(windowMax(x, k, false), windowMaxBrute(x, k)))
                throw new IllegalStateException("mismatch on " + Arrays.toString(x) + " k=" + k);
        }
        System.out.println("10000 random cases agree with brute force");
    }
}
