import java.util.Random;

public class Birthday {
    // Exact: P(no match) = (d/d) * ((d-1)/d) * ... * ((d-n+1)/d)
    static double pShared(int n, double d) {
        double noMatch = 1.0;
        for (int i = 0; i < n; i++) noMatch *= 1 - i / d;
        return 1 - noMatch;
    }

    // Monte Carlo: fill n random birthdays, stop at the first repeat
    static double simulate(int n, int d, int trials, Random rnd) {
        int hits = 0;
        for (int t = 0; t < trials; t++) {
            boolean[] seen = new boolean[d];
            for (int i = 0; i < n; i++) {
                int day = rnd.nextInt(d);
                if (seen[day]) { hits++; break; }
                seen[day] = true;
            }
        }
        return (double) hits / trials;
    }

    public static void main(String[] args) {
        Random rnd = new Random(42);
        for (int n : new int[]{10, 23, 50, 70}) {
            System.out.printf("n=%2d  exact %.4f  simulated %.4f%n", n, pShared(n, 365.0), simulate(n, 365, 100_000, rnd));
        }
        // Expected number of matching PAIRS = C(n,2) / d, by linearity of expectation
        System.out.printf("n=23  expected matching pairs = %.3f%n", 23 * 22 / 2 / 365.0);

        // Hash codes: d = 2^32 possible int values
        double d = 4_294_967_296.0;
        int n = 1;
        while (pShared(n, d) < 0.5) n++;
        System.out.printf("2^32 hash codes: 50%% chance of a collision at n = %,d%n", n);
    }
}
