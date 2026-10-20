import java.util.Random;

public class Birthday {
    // How many random keys until two land in the same bucket?
    static int firstCollision(int buckets, Random rnd) {
        boolean[] used = new boolean[buckets];
        int keys = 0;
        while (true) {
            int i = rnd.nextInt(buckets);
            keys++;
            if (used[i]) return keys;
            used[i] = true;
        }
    }

    public static void main(String[] args) {
        Random rnd = new Random(42);
        int trials = 100_000;

        int shared = 0;
        for (int t = 0; t < trials; t++) if (firstCollision(365, rnd) <= 23) shared++;
        System.out.println("23 people, 365 days: shared birthday in " + 100.0 * shared / trials + "% of trials");

        double exact = 1.0;
        for (int k = 0; k < 23; k++) exact *= (365.0 - k) / 365;
        System.out.printf("exact: %.1f%%%n", 100 * (1 - exact));

        long total = 0;
        for (int t = 0; t < 1_000; t++) total += firstCollision(1_000_000, rnd);
        System.out.println("1,000,000 buckets: first collision after ~" + total / 1_000 + " keys on average");
    }
}
