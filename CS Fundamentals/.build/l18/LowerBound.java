public class LowerBound {
    // log2(n!) = log2(1) + log2(2) + ... + log2(n), so it never overflows.
    static double log2Factorial(int n) {
        double sum = 0;
        for (int k = 1; k <= n; k++) sum += Math.log(k) / Math.log(2);
        return sum;
    }

    public static void main(String[] args) {
        System.out.println("        n   leaves n!   min compares ceil(log2 n!)   n log2 n");
        for (int n : new int[] {3, 4, 10, 1_000, 1_000_000}) {
            String leaves = "huge";
            if (n <= 10) {
                long f = 1;
                for (int k = 2; k <= n; k++) f *= k;
                leaves = String.format("%,d", f);
            }
            long minCompares = (long) Math.ceil(log2Factorial(n));
            long nLogN = (long) (n * (Math.log(n) / Math.log(2)));
            System.out.println(String.format("%,9d %12s %28s %10s", n, leaves,
                String.format("%,d", minCompares), String.format("%,d", nLogN)));
        }
    }
}
