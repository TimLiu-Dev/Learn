import java.util.function.IntUnaryOperator;

public class Series {
    // Count the element copies a growing array makes while n items are appended.
    static long copies(int n, IntUnaryOperator grow) {
        int capacity = 1;
        long total = 0;
        for (int size = 0; size < n; size++) {
            if (size == capacity) { total += size; capacity = grow.applyAsInt(capacity); }   // copy everything across
        }
        return total;
    }

    public static void main(String[] args) {
        // Arithmetic series: the inner loop runs 0 + 1 + ... + (n-1) times
        int n = 1000;
        long pairs = 0;
        for (int i = 0; i < n; i++) for (int j = 0; j < i; j++) pairs++;
        System.out.println("pairs i>j for n=" + n + ": " + pairs + "   formula n(n-1)/2 = " + (long) n * (n - 1) / 2);

        // Geometric series: 1 + 2 + 4 + ... + 2^k = 2^(k+1) - 1
        int geo = 0;
        for (int k = 0; k <= 9; k++) geo += 1 << k;
        System.out.println("1+2+4+...+512 = " + geo + "   formula 2^10 - 1 = " + ((1 << 10) - 1));

        System.out.println("n          doubling copies  per item   +10 copies    per item");
        for (int items : new int[]{1_000, 10_000, 100_000, 1_000_000}) {
            long d = copies(items, c -> c * 2);
            long a = copies(items, c -> c + 10);
            System.out.printf("%-10d %15d  %8.2f  %12d  %10.1f%n", items, d, (double) d / items, a, (double) a / items);
        }
    }
}
