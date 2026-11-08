import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Sat {
    // CNF: an AND of clauses, each an OR of literals. Literal +v means x_v, -v means NOT x_v (v = 1..n).
    // An assignment is a long whose bit v-1 holds the value of x_v (L48).
    static boolean satisfies(List<int[]> formula, long a) {      // the verifier: O(size of formula)
        for (int[] clause : formula) {
            boolean ok = false;
            for (int lit : clause) {
                boolean bit = ((a >> (Math.abs(lit) - 1)) & 1L) == 1L;
                if (bit == (lit > 0)) { ok = true; break; }
            }
            if (!ok) return false;
        }
        return true;
    }

    static List<int[]> randomFormula(int n, int m, Random rnd) {
        List<int[]> f = new ArrayList<>();
        List<Integer> vars = new ArrayList<>();
        for (int v = 1; v <= n; v++) vars.add(v);
        for (int k = 0; k < m; k++) {
            Collections.shuffle(vars, rnd);                       // 3 different variables per clause
            int[] clause = new int[3];
            for (int i = 0; i < 3; i++) clause[i] = rnd.nextBoolean() ? vars.get(i) : -vars.get(i);
            f.add(clause);
        }
        return f;
    }

    static long tried;
    static Long bruteForce(int n, List<int[]> formula) {          // the searcher: up to 2^n tries
        tried = 0;
        for (long a = 0; a < (1L << n); a++) { tried++; if (satisfies(formula, a)) return a; }
        return null;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) bruteForce(12, randomFormula(12, 60, new Random(i)));  // warm-up
        double rate = 0;
        for (int n = 16; n <= 24; n += 2) {
            List<int[]> f = randomFormula(n, 5 * n, new Random(n));
            long t0 = System.nanoTime();
            Long answer = bruteForce(n, f);
            long ns = System.nanoTime() - t0;
            rate = tried / (ns / 1e9);
            System.out.printf("n=%2d clauses=%3d  %-5s tried %,10d of %,10d  %7d ms%n",
                n, f.size(), answer == null ? "UNSAT" : "SAT", tried, 1L << n, ns / 1_000_000);
        }
        // Checking a proposed answer is cheap even when finding one isn't
        Random rnd = new Random(60);
        long secret = rnd.nextLong() >>> 4;                       // a 60-variable assignment
        List<int[]> f = randomFormula(60, 300, rnd);
        for (int[] c : f) if (!satisfies(List.<int[]>of(c), secret)) c[0] = -c[0];  // make it agree
        for (int i = 0; i < 1000; i++) satisfies(f, secret);      // warm-up
        long t0 = System.nanoTime();
        satisfies(f, secret);
        System.out.printf("n=60 clauses=300: checking the answer took %.1f µs%n", (System.nanoTime() - t0) / 1000.0);
        System.out.printf("brute force at %.0f million tries/s would need ~%,.0f years%n",
            rate / 1e6, (1L << 60) / rate / 3600 / 24 / 365);
    }
}
