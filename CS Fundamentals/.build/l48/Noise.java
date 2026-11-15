import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class Noise {
    static double mean(List<Double> xs) {
        double sum = 0;
        for (double x : xs) sum += x;
        return sum / xs.size();
    }

    // Var[X] = E[(X - E[X])²], the mean square distance from the mean
    static double variance(List<Double> xs) {
        double m = mean(xs), sum = 0;
        for (double x : xs) sum += (x - m) * (x - m);
        return sum / xs.size();
    }

    static double sd(List<Double> xs) { return Math.sqrt(variance(xs)); }

    public static void main(String[] args) {
        // Two batches of ten timings (ms) of the SAME code, sorting 1,000,000 ints, captured in one real run
        List<Double> a = List.of(42.6, 42.1, 42.3, 45.2, 42.5, 40.5, 39.9, 41.3, 41.7, 41.9);
        List<Double> b = List.of(41.7, 41.8, 41.4, 41.0, 41.4, 41.2, 41.3, 41.1, 41.3, 41.5);
        for (var entry : List.of(java.util.Map.entry("A", a), java.util.Map.entry("B", b))) {
            List<Double> xs = entry.getValue();
            List<Double> squares = xs.stream().map(x -> x * x).toList();
            double shortcut = mean(squares) - mean(xs) * mean(xs);             // E[X²] - E[X]²
            double sampleSd = Math.sqrt(variance(xs) * xs.size() / (xs.size() - 1));   // divides by n - 1 instead
            System.out.printf("%s: mean %.2f  variance %.3f (shortcut %.3f)  sd %.2f  (n-1 version %.2f)%n",
                entry.getKey(), mean(xs), variance(xs), shortcut, sd(xs), sampleSd);
        }
        double gap = mean(a) - mean(b);
        double gapSd = Math.sqrt(variance(a) / a.size() + variance(b) / b.size());   // variances of independent things add
        System.out.printf("gap of the means %.2f ms, noise in that gap (sd) %.2f ms%n", gap, gapSd);

        // Averaging n independent runs divides the sd by sqrt(n). Model: 42 ms plus noise with sd 1.0 ms.
        Random rnd = new Random(16);
        System.out.println("runs averaged   sd of the average   1.0 / sqrt(n)");
        for (int n : new int[]{1, 4, 16, 64}) {
            List<Double> averages = new ArrayList<>();
            for (int t = 0; t < 20_000; t++) {
                double sum = 0;
                for (int i = 0; i < n; i++) sum += 42.0 + rnd.nextGaussian();
                averages.add(sum / n);
            }
            System.out.printf("%-14d  %-18.3f  %.3f%n", n, sd(averages), 1 / Math.sqrt(n));
        }
    }
}
