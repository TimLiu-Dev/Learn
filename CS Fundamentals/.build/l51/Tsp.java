import java.util.Random;

public class Tsp {
    static class Cities {
        final double[] x, y;
        Cities(int n, long seed) {
            Random rnd = new Random(seed);
            x = new double[n]; y = new double[n];
            for (int i = 0; i < n; i++) x[i] = rnd.nextDouble() * 100;
            for (int i = 0; i < n; i++) y[i] = rnd.nextDouble() * 100;
        }
        int n() { return x.length; }
        double d(int a, int b) { return Math.hypot(x[a] - x[b], y[a] - y[b]); }
    }

    // Exact: try every tour that starts at city 0, built by backtracking (L50 permutations).
    static double best;
    static double bruteForce(Cities c) {
        best = Double.MAX_VALUE;
        boolean[] used = new boolean[c.n()];
        used[0] = true;
        extend(c, used, 0, 1, 0.0);
        return best;
    }

    static void extend(Cities c, boolean[] used, int last, int count, double length) {
        if (length >= best) return;                                   // prune: already too long
        if (count == c.n()) { best = Math.min(best, length + c.d(last, 0)); return; }
        for (int next = 1; next < c.n(); next++) {
            if (used[next]) continue;
            used[next] = true;
            extend(c, used, next, count + 1, length + c.d(last, next));
            used[next] = false;
        }
    }

    // Heuristic: always walk to the nearest city you haven't visited. O(n²), no guarantee.
    static double nearestNeighbour(Cities c) {
        boolean[] used = new boolean[c.n()];
        used[0] = true;
        int at = 0;
        double length = 0;
        for (int step = 1; step < c.n(); step++) {
            int bestJ = -1;
            for (int j = 0; j < c.n(); j++)
                if (!used[j] && (bestJ < 0 || c.d(at, j) < c.d(at, bestJ))) bestJ = j;
            used[bestJ] = true; length += c.d(at, bestJ); at = bestJ;
        }
        return length + c.d(at, 0);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) bruteForce(new Cities(8, i));       // warm-up
        System.out.println(" n  tours to try (n-1)!   exact ms   exact len   greedy len   greedy/exact");
        for (int n = 8; n <= 15; n++) {
            Cities c = new Cities(n, n);
            long t0 = System.nanoTime();
            double exact = bruteForce(c);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            double greedy = nearestNeighbour(c);
            long tours = 1;
            for (int k = 1; k <= n - 1; k++) tours *= k;
            System.out.printf("%2d  %19s  %9d  %10.1f  %11.1f  %13.2f%n",
                n, String.format("%,d", tours), ms, exact, greedy, greedy / exact);
        }
        Cities big = new Cities(10_000, 1);
        long t0 = System.nanoTime();
        nearestNeighbour(big);
        System.out.println("nearest neighbour on 10,000 cities: " + (System.nanoTime() - t0) / 1_000_000 + " ms");
    }
}
