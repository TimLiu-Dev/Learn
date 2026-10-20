import java.util.function.LongSupplier;

public class Grid {
    static final int N = 4096;                      // 4096 x 4096 ints = 64 MB

    static long rowMajor(int[][] g) {
        long sum = 0;
        for (int r = 0; r < N; r++) for (int c = 0; c < N; c++) sum += g[r][c]; // walk along a row
        return sum;
    }

    static long colMajor(int[][] g) {
        long sum = 0;
        for (int c = 0; c < N; c++) for (int r = 0; r < N; r++) sum += g[r][c]; // jump to a new row every step
        return sum;
    }

    static double[] bestMs(LongSupplier f) {
        double best = Double.MAX_VALUE;
        long result = 0;
        for (int i = 0; i < 5; i++) {
            long t = System.nanoTime();
            result = f.getAsLong();
            best = Math.min(best, (System.nanoTime() - t) / 1e6);
        }
        return new double[] {best, result};
    }

    public static void main(String[] args) {
        int[][] g = new int[N][N];
        for (int r = 0; r < N; r++) for (int c = 0; c < N; c++) g[r][c] = (r + c) % 7;
        double[] row = bestMs(() -> rowMajor(g));
        double[] col = bestMs(() -> colMajor(g));
        System.out.printf("row-major:    %6.1f ms  (sum %d)%n", row[0], (long) row[1]);
        System.out.printf("column-major: %6.1f ms  (sum %d)%n", col[0], (long) col[1]);
        System.out.printf("column-major is %.1fx slower%n", col[0] / row[0]);
    }
}
