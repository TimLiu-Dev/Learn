import java.util.function.LongSupplier;

public class Flat {
    static final int N = 4096;

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
        int[][] nested = new int[N][N];                 // N separate row objects
        int[] flat = new int[N * N];                    // one block; cell (r, c) at r * N + c
        for (int r = 0; r < N; r++)
            for (int c = 0; c < N; c++) { nested[r][c] = (r + c) % 7; flat[r * N + c] = (r + c) % 7; }

        String[] names = {"nested, row-major", "flat, row-major", "nested, column-major", "flat, column-major"};
        double[][] res = {
            bestMs(() -> { long s = 0; for (int r = 0; r < N; r++) for (int c = 0; c < N; c++) s += nested[r][c]; return s; }),
            bestMs(() -> { long s = 0; for (int r = 0; r < N; r++) for (int c = 0; c < N; c++) s += flat[r * N + c]; return s; }),
            bestMs(() -> { long s = 0; for (int c = 0; c < N; c++) for (int r = 0; r < N; r++) s += nested[r][c]; return s; }),
            bestMs(() -> { long s = 0; for (int c = 0; c < N; c++) for (int r = 0; r < N; r++) s += flat[r * N + c]; return s; }),
        };
        for (int i = 0; i < res.length; i++)
            System.out.printf("%-21s %6.1f ms  (sum %d)%n", names[i], res[i][0], (long) res[i][1]);
    }
}
