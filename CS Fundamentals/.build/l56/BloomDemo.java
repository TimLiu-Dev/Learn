// Measure a Bloom filter's false-positive rate and compare it with the formula (1 - e^(-kn/m))^k.
public class BloomDemo {
    public static void main(String[] args) {
        int n = 100_000;
        int probes = 1_000_000;
        System.out.println("bits/key   k   memory    measured   predicted");
        int[][] runs = { {4, 0}, {8, 0}, {10, 0}, {10, 1}, {16, 0} };
        for (int[] run : runs) {
            int m = n * run[0];
            int k = run[1] > 0 ? run[1] : Bloom.optimalK(m, n);
            Bloom bloom = new Bloom(m, k);
            for (int i = 0; i < n; i++) bloom.add("key-" + i);
            for (int i = 0; i < n; i++) if (!bloom.mightContain("key-" + i)) throw new IllegalStateException("false no");
            int fp = 0;
            for (int i = 0; i < probes; i++) if (bloom.mightContain("absent-" + i)) fp++;
            System.out.println(String.format("%8d  %2d  %5d KB    %6.3f%%    %6.3f%%",
                    run[0], k, m / 8 / 1024, 100.0 * fp / probes, 100 * Bloom.predicted(m, n, k)));
        }
    }
}
