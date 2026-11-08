import java.util.*;

public class ShortIds {
    static double pShared(int n, double d) {
        double noMatch = 1.0;
        for (int i = 0; i < n; i++) noMatch *= 1 - i / d;
        return 1 - noMatch;
    }

    // Smallest n whose collision probability reaches p
    static int firstN(double d, double p) {
        int n = 1;
        while (pShared(n, d) < p) n++;
        return n;
    }

    public static void main(String[] args) {
        double big = Math.pow(36, 6);                 // 6 chars of [a-z0-9]
        System.out.printf("d = %,.0f%n", big);
        System.out.printf("1%%: n = %,d   50%%: n = %,d%n", firstN(big, 0.01), firstN(big, 0.5));

        // Check the maths by simulation on a space small enough to simulate: 3 chars
        int small = 36 * 36 * 36;
        int n = firstN(small, 0.5);
        Random rnd = new Random(7);
        int trials = 20_000, hits = 0;
        for (int t = 0; t < trials; t++) {
            Set<Integer> seen = new HashSet<>();
            for (int i = 0; i < n; i++) if (!seen.add(rnd.nextInt(small))) { hits++; break; }
        }
        System.out.printf("d = %d: predicted 50%% at n = %d, simulated %.3f%n", small, n, (double) hits / trials);
    }
}
