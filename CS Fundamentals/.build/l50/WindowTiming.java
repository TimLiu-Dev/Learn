import java.util.Arrays;
import java.util.function.IntSupplier;

// Both versions track characters in a 65,536-slot int[] (one per char value, L2),
// so the timing compares the algorithms, not HashMap overhead.
public class WindowTiming {
    // Restart from every start position: O(n * window length), O(n^2) when nothing repeats.
    static int longestUniqueBrute(String s) {
        int[] stamp = new int[65_536];                   // stamp[c] == start + 1 means "seen from this start"
        int best = 0;
        for (int start = 0; start < s.length(); start++) {
            int end = start;
            while (end < s.length() && stamp[s.charAt(end)] != start + 1) {
                stamp[s.charAt(end)] = start + 1;
                end++;
            }
            best = Math.max(best, end - start);
        }
        return best;
    }

    // The sliding window: each index enters once and the left edge only moves forward. O(n).
    static int longestUniqueWindow(String s) {
        int[] lastSeen = new int[65_536];
        Arrays.fill(lastSeen, -1);
        int left = 0, best = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastSeen[c] >= left) left = lastSeen[c] + 1;
            lastSeen[c] = right;
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    static long sink = 0;  // L16: every result is used

    static double bestOf3Ms(IntSupplier f) {
        for (int i = 0; i < 10; i++) sink += f.getAsInt();     // warm-up: these calls are fast, so give the JIT more of them
        double best = Double.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            long t0 = System.nanoTime();
            sink += f.getAsInt();
            best = Math.min(best, (System.nanoTime() - t0) / 1e6);
        }
        return best;
    }

    public static void main(String[] args) {
        System.out.println("worst case: n distinct characters (nothing ever repeats)");
        System.out.println("         n      brute ms  ratio     window ms");
        double prev = 0;
        for (int n : new int[] {2_500, 5_000, 10_000, 20_000}) {
            char[] cs = new char[n];
            for (int i = 0; i < n; i++) cs[i] = (char) (0x4E00 + i);   // n different CJK characters
            String s = new String(cs);
            if (longestUniqueBrute(s) != n || longestUniqueWindow(s) != n) throw new IllegalStateException();
            double b = bestOf3Ms(() -> longestUniqueBrute(s));
            double w = bestOf3Ms(() -> longestUniqueWindow(s));
            String ratio = prev > 0 ? String.format("%.1f", b / prev) : "-";
            System.out.printf("%,10d  %12.3f  %5s  %12.3f%n", n, b, ratio, w);
            prev = b;
        }
        System.out.println("(sink " + sink + ")");
    }
}
