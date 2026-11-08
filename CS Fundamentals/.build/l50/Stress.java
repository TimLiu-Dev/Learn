import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

// Practise without an online judge: check a fast solution against an obviously correct slow one
// on thousands of small random inputs, and print the first input where they disagree.
public class Stress {
    static <T, R> boolean stress(int trials, BiFunction<Random, Integer, T> gen,
                                 Function<T, R> slow, Function<T, R> fast, Function<T, String> show) {
        Random rnd = new Random(50);
        for (int t = 0; t < trials; t++) {
            T input = gen.apply(rnd, 1 + t / 1000);      // small inputs first: counterexamples stay readable
            R want = slow.apply(input);
            R got = fast.apply(input);
            if (!Objects.equals(want, got)) {
                System.out.println("trial " + t + ": " + show.apply(input) + "  slow=" + want + " fast=" + got);
                return false;
            }
        }
        System.out.println(trials + " trials, no difference");
        return true;
    }

    record Case(int[] a, int target) {}

    static int shortestSlow(Case c) {                    // try every start and end: O(n^2), easy to trust
        int best = 0;
        for (int i = 0; i < c.a().length; i++) {
            int sum = 0;
            for (int j = i; j < c.a().length; j++) {
                sum += c.a()[j];
                if (sum >= c.target()) { if (best == 0 || j - i + 1 < best) best = j - i + 1; break; }
            }
        }
        return best;
    }

    static int shortestBuggy(Case c) {                   // `if` where the window needs `while`
        int left = 0, sum = 0, best = Integer.MAX_VALUE;
        for (int right = 0; right < c.a().length; right++) {
            sum += c.a()[right];
            if (sum >= c.target()) { best = Math.min(best, right - left + 1); sum -= c.a()[left++]; }
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }

    static int shortestFixed(Case c) {
        int left = 0, sum = 0, best = Integer.MAX_VALUE;
        for (int right = 0; right < c.a().length; right++) {
            sum += c.a()[right];
            while (sum >= c.target()) { best = Math.min(best, right - left + 1); sum -= c.a()[left++]; }
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }

    public static void main(String[] args) {
        BiFunction<Random, Integer, Case> gen = (rnd, size) -> {
            int[] a = new int[rnd.nextInt(1, size + 4)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(1, 6);
            return new Case(a, rnd.nextInt(1, 15));
        };
        Function<Case, String> show = c -> "a=" + Arrays.toString(c.a()) + " target=" + c.target();
        stress(10_000, gen, Stress::shortestSlow, Stress::shortestBuggy, show);
        stress(10_000, gen, Stress::shortestSlow, Stress::shortestFixed, show);
    }
}
