import java.util.Arrays;
import java.util.Random;

// Exercise solution: how far off is a percentile read from histogram buckets?
public class Percentiles {
    static long bucketEstimate(long[] sorted, long[] bounds, double p) {
        double target = p * sorted.length;
        for (long b : bounds) if (Arrays.stream(sorted).filter(x -> x <= b).count() >= target) return b;
        return Long.MAX_VALUE;
    }

    public static void main(String[] args) {
        Random random = new Random(43);
        // 10,000 request times: mostly 8–37 ms, 4% slow ones between 300 and 999 ms
        long[] ms = new long[10_000];
        for (int i = 0; i < ms.length; i++) ms[i] = random.nextInt(100) < 4 ? 300 + random.nextInt(700) : 8 + random.nextInt(30);
        long[] sorted = ms.clone();
        Arrays.sort(sorted);
        long[] coarse = {5, 10, 25, 50, 100, 250, 500, 1000};
        long[] fine = {5, 10, 15, 20, 25, 30, 35, 40, 50, 100, 250, 400, 600, 800, 1000};
        System.out.println("     exact   coarse buckets   fine buckets");
        for (double p : new double[] {0.50, 0.95, 0.99}) {
            long exact = sorted[Math.min((int) (p * sorted.length), sorted.length - 1)];
            System.out.printf("p%-3d %4d ms   ≤ %4d ms        ≤ %4d ms%n", (int) (p * 100), exact,
                    bucketEstimate(sorted, coarse, p), bucketEstimate(sorted, fine, p));
        }
    }
}
