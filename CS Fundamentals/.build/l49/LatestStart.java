import java.util.*;
import java.util.function.ToIntFunction;

public class LatestStart {
    // Half-open [start, end): a class may start the minute another one ends.
    record Interval(String name, int start, int end) {}

    static boolean overlaps(Interval a, Interval b) { return a.start() < b.end() && b.start() < a.end(); }

    // Every greedy rule here has the same shape: sort by a key, never look back.
    static List<Interval> greedy(List<Interval> classes, ToIntFunction<Interval> key) {
        List<Interval> sorted = new ArrayList<>(classes);
        sorted.sort(Comparator.comparingInt(key));          // stable, like Kotlin's sortedBy
        List<Interval> chosen = new ArrayList<>();
        for (Interval c : sorted) {
            boolean fits = true;
            for (Interval x : chosen) if (overlaps(x, c)) { fits = false; break; }
            if (fits) chosen.add(c);
        }
        return chosen;
    }

    // The truth, by brute force: try all 2^n subsets (bit i of mask = take class i).
    static int bestSize(List<Interval> classes) {
        int best = 0;
        for (int mask = 0; mask < (1 << classes.size()); mask++) {
            List<Interval> pick = new ArrayList<>();
            for (int i = 0; i < classes.size(); i++) if ((mask & (1 << i)) != 0) pick.add(classes.get(i));
            boolean clashFree = true;
            for (int i = 0; i < pick.size() && clashFree; i++)
                for (int j = i + 1; j < pick.size(); j++)
                    if (overlaps(pick.get(i), pick.get(j))) { clashFree = false; break; }
            if (clashFree) best = Math.max(best, pick.size());
        }
        return best;
    }

    static List<String> names(List<Interval> xs) { return xs.stream().map(Interval::name).toList(); }

    public static void main(String[] args) {
        Map<String, ToIntFunction<Interval>> rules = new LinkedHashMap<>();
        rules.put("earliest start", Interval::start);
        rules.put("shortest first", c -> c.end() - c.start());
        rules.put("earliest finish", Interval::end);
        rules.put("latest start", c -> -c.start());          // the mirror image: sort by start, descending

        var trap1 = List.of(new Interval("long", 0, 10), new Interval("a", 1, 2), new Interval("b", 3, 4));
        var trap2 = List.of(new Interval("x", 0, 5), new Interval("short", 4, 7), new Interval("y", 6, 11));
        rules.forEach((name, key) -> System.out.println(String.format("%-16s trap1 -> %s   trap2 -> %s",
            name, names(greedy(trap1, key)), names(greedy(trap2, key)))));

        Random rnd = new Random(49);
        Map<String, Integer> wins = new LinkedHashMap<>();
        rules.keySet().forEach(n -> wins.put(n, 0));
        int trials = 10_000;
        for (int t = 0; t < trials; t++) {
            List<Interval> classes = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                int s = rnd.nextInt(20);
                classes.add(new Interval("c" + i, s, s + 1 + rnd.nextInt(8)));
            }
            int best = bestSize(classes);
            rules.forEach((name, key) -> { if (greedy(classes, key).size() == best) wins.merge(name, 1, Integer::sum); });
        }
        System.out.println("random tests: " + trials + " schedules of 8 classes each");
        wins.forEach((name, w) -> System.out.println(String.format("%-16s optimal in %5d of %d", name, w, trials)));
    }
}
