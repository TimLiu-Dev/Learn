// From k = ln2 · m/n and f = (1/2)^k: bits per key for a target false-positive rate p is -ln(p) / (ln 2)².
public class BloomSize {
    static double bitsPerKey(double p) { return -Math.log(p) / (Math.log(2) * Math.log(2)); }

    public static void main(String[] args) {
        int n = 100_000;
        for (double p : new double[] { 0.10, 0.01, 0.001 }) {
            int m = (int) Math.ceil(n * bitsPerKey(p));
            int k = (int) Math.round(Math.log(2) * m / n);
            Bloom bloom = new Bloom(m, k);
            for (int i = 0; i < n; i++) bloom.add("key-" + i);
            int fp = 0;
            for (int i = 0; i < 1_000_000; i++) if (bloom.mightContain("absent-" + i)) fp++;
            System.out.println(String.format("target %5.1f%%: %5.2f bits/key, k = %2d, measured %6.3f%%",
                    100 * p, (double) m / n, k, fp / 10_000.0));
        }
    }
}
