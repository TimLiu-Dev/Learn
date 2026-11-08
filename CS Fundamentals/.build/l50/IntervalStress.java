import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.ToIntFunction;

// Stress-test three greedy rules for "most non-overlapping meetings" (L49) against
// a backtracking brute force that tries every subset (Part 2).
public class IntervalStress {
    record Meeting(int start, int end) {}                // half-open [start, end)

    static boolean fits(List<Meeting> chosen, Meeting m) {
        for (Meeting c : chosen) if (!(c.end() <= m.start() || m.end() <= c.start())) return false;
        return true;
    }

    static int best;

    static int bruteMax(List<Meeting> ms) {
        best = 0;
        go(ms, 0, new ArrayList<>());
        return best;
    }

    private static void go(List<Meeting> ms, int i, List<Meeting> chosen) {
        if (i == ms.size()) { best = Math.max(best, chosen.size()); return; }
        go(ms, i + 1, chosen);                           // skip ms[i]
        if (fits(chosen, ms.get(i))) {                   // prune: only compatible picks
            chosen.add(ms.get(i)); go(ms, i + 1, chosen); chosen.removeLast();
        }
    }

    static int greedy(List<Meeting> ms, Comparator<Meeting> order) {
        int count = 0, freeFrom = Integer.MIN_VALUE;
        for (Meeting m : ms.stream().sorted(order).toList()) if (m.start() >= freeFrom) { count++; freeFrom = m.end(); }
        return count;
    }

    static int greedyAny(List<Meeting> ms, Comparator<Meeting> order) {   // for rules that don't scan left to right
        List<Meeting> chosen = new ArrayList<>();
        for (Meeting m : ms.stream().sorted(order).toList()) if (fits(chosen, m)) chosen.add(m);
        return chosen.size();
    }

    public static void main(String[] args) {
        Map<String, ToIntFunction<List<Meeting>>> rules = new LinkedHashMap<>();
        rules.put("earliest finish", ms -> greedy(ms, Comparator.comparingInt(Meeting::end)));
        rules.put("earliest start", ms -> greedy(ms, Comparator.comparingInt(Meeting::start)));
        rules.put("shortest first", ms -> greedyAny(ms, Comparator.comparingInt(m -> m.end() - m.start())));
        for (var e : rules.entrySet()) {
            Random rnd = new Random(50);
            List<Meeting> bad = null;
            for (int t = 0; t < 5_000 && bad == null; t++) {
                List<Meeting> ms = new ArrayList<>();
                int n = rnd.nextInt(1, Math.min(8, 2 + t / 500));    // small inputs first
                for (int i = 0; i < n; i++) { int s = rnd.nextInt(0, 10); ms.add(new Meeting(s, s + rnd.nextInt(1, 5))); }
                if (e.getValue().applyAsInt(ms) != bruteMax(ms)) bad = ms;
            }
            String verdict = bad == null ? "5000 trials, always optimal"
                    : "WRONG on " + bad + ": greedy " + e.getValue().applyAsInt(bad) + ", best " + bruteMax(bad);
            System.out.printf("%-15s %s%n", e.getKey(), verdict);
        }
    }
}
