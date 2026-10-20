import java.util.*;
import java.util.function.Function;

public class Ex2 {
    static List<String> uniqueSlow(List<String> words) {
        List<String> result = new ArrayList<>();
        for (String w : words) if (!result.contains(w)) result.add(w);
        return result;
    }

    static List<String> uniqueFast(List<String> words) {
        Set<String> seen = new HashSet<>();
        List<String> result = new ArrayList<>();
        for (String w : words) if (seen.add(w)) result.add(w);
        return result;
    }

    // n words, about half of them repeats
    static List<String> makeWords(int n) {
        List<String> words = new ArrayList<>();
        for (int i = 0; i < n; i++) words.add("w" + (i * 7919) % (n / 2));
        return words;
    }

    static double bestMs(List<String> words, Function<List<String>, List<String>> f) {
        int sink = 0;
        for (int i = 0; i < 3; i++) sink += f.apply(words).size();   // warm-up
        double best = Double.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            long t0 = System.nanoTime();
            sink += f.apply(words).size();
            best = Math.min(best, (System.nanoTime() - t0) / 1e6);
        }
        if (sink == 0) throw new IllegalStateException();          // use the results
        return best;
    }

    public static void main(String[] args) {
        System.out.printf("%7s %10s %6s %10s %6s%n", "n", "slow ms", "ratio", "fast ms", "ratio");
        double prevSlow = 0, prevFast = 0;
        for (int n : new int[] {4_000, 8_000, 16_000, 32_000, 64_000}) {
            List<String> words = makeWords(n);
            double slow = bestMs(words, Ex2::uniqueSlow);
            double fast = bestMs(words, Ex2::uniqueFast);
            String rs = prevSlow > 0 ? String.format("%.1f", slow / prevSlow) : "-";
            String rf = prevFast > 0 ? String.format("%.1f", fast / prevFast) : "-";
            System.out.printf("%7d %10.2f %6s %10.2f %6s%n", n, slow, rs, fast, rf);
            prevSlow = slow;
            prevFast = fast;
        }
    }
}
