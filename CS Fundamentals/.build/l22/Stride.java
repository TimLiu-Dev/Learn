public class Stride {
    static final int SIZE = 64 * 1024 * 1024;        // 64 M ints = 256 MB, far bigger than any cache

    static double touch(int[] a, int step) {
        double best = Double.MAX_VALUE;
        for (int r = 0; r < 5; r++) {
            long t = System.nanoTime();
            for (int i = 0; i < SIZE; i += step) a[i]++; // SIZE / step accesses
            best = Math.min(best, (System.nanoTime() - t) / 1e6);
        }
        return best;
    }

    public static void main(String[] args) {
        int[] a = new int[SIZE];
        System.out.println("step  accesses    total ms   ns/access");
        for (int step : new int[] {1, 2, 4, 8, 16, 32, 64, 128}) {
            double ms = touch(a, step);
            int n = SIZE / step;
            System.out.printf("%4d  %10d  %8.1f  %8.2f%n", step, n, ms, ms * 1e6 / n);
        }
    }
}
