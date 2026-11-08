// N-queens: place one queen per row so that no two share a column or a diagonal.
public class Queens {
    private final int n;
    private final boolean[] cols, diag1, diag2;          // diag1: row + col; diag2: row - col + n
    long nodes = 0;                                      // partial placements tried

    Queens(int n) {
        this.n = n;
        cols = new boolean[n];
        diag1 = new boolean[2 * n];
        diag2 = new boolean[2 * n];
    }

    long count(int row) {
        nodes++;
        if (row == n) return 1;
        long total = 0;
        for (int c = 0; c < n; c++) {
            if (cols[c] || diag1[row + c] || diag2[row - c + n]) continue;      // prune: attacked already
            cols[c] = diag1[row + c] = diag2[row - c + n] = true;               // choose
            total += count(row + 1);                                             // explore
            cols[c] = diag1[row + c] = diag2[row - c + n] = false;              // un-choose
        }
        return total;
    }

    // No pruning: try all n^n ways to put one queen in each row, check only at the end.
    static long bruteOk, bruteLeaves;

    static void brute(int[] q, int row) {
        int n = q.length;
        if (row == n) {
            bruteLeaves++;
            for (int i = 0; i < n; i++)
                for (int j = i + 1; j < n; j++)
                    if (q[i] == q[j] || j - i == Math.abs(q[j] - q[i])) return;
            bruteOk++;
            return;
        }
        for (int c = 0; c < n; c++) { q[row] = c; brute(q, row + 1); }
    }

    public static void main(String[] args) {
        long[] oeis = {1, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};   // OEIS A000170
        System.out.println(" n  solutions  OEIS       nodes       ms");
        for (int n = 1; n <= 13; n++) {
            Queens q = new Queens(n);
            long t0 = System.nanoTime();
            long sols = q.count(0);
            double ms = (System.nanoTime() - t0) / 1e6;
            if (sols != oeis[n]) throw new IllegalStateException("n=" + n);
            System.out.printf("%2d %10d %5s %11d %8.1f%n", n, sols, "ok", q.nodes, ms);
        }
        long t0 = System.nanoTime();
        brute(new int[8], 0);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.println("brute force n=8: " + bruteOk + " solutions after checking " + bruteLeaves
                + " complete boards (" + ms + " ms)");
    }
}
