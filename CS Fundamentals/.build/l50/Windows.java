import java.util.HashMap;
import java.util.Map;

// Sliding windows: a range [left, right] that moves through the input instead of restarting.
public class Windows {
    // Fixed size k: add the element entering on the right, subtract the one leaving on the left.
    static int maxSumOfK(int[] a, int k) {
        if (k < 1 || k > a.length) throw new IllegalArgumentException("k must be in 1.." + a.length);
        int sum = 0;
        for (int i = 0; i < k; i++) sum += a[i];             // the first window
        int best = sum;
        for (int right = k; right < a.length; right++) {
            sum += a[right] - a[right - k];                  // slide by one: O(1), not O(k)
            best = Math.max(best, sum);
        }
        return best;
    }

    // Variable size: grow on the right; when the window breaks the rule, shrink from the left.
    // Rule here: no character repeats inside the window.
    static String longestUnique(String s, boolean trace) {
        Map<Character, Integer> lastSeen = new HashMap<>();  // char -> index of its latest occurrence
        int left = 0, bestStart = 0, bestLen = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            Integer prev = lastSeen.get(c);
            if (prev != null && prev >= left) left = prev + 1;   // jump past the earlier copy of c
            lastSeen.put(c, right);
            if (right - left + 1 > bestLen) { bestLen = right - left + 1; bestStart = left; }
            if (trace) System.out.println("  right=" + right + " '" + c + "'  window=\"" + s.substring(left, right + 1) + "\"");
        }
        return s.substring(bestStart, bestStart + bestLen);
    }

    public static void main(String[] args) {
        int[] temps = {3, 8, 1, 9, 4, 6, 2, 7};
        System.out.println("best 3-day total: " + maxSumOfK(temps, 3));
        System.out.println("longest without repeats:");
        System.out.println("-> \"" + longestUnique("abcabcbb", true) + "\"");
        System.out.println("\"" + longestUnique("pwwkew", false) + "\" \"" + longestUnique("", false)
                + "\" \"" + longestUnique("aaaa", false) + "\"");
    }
}
