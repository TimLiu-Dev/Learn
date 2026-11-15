import java.util.Random;

public class Bayes {
    // Bayes' rule: P(outage | alert) = P(alert | outage) * P(outage) / P(alert),
    // where P(alert) adds up both ways an alert can happen (the law of total probability).
    static double pOutageGivenAlert(double prior, double hitRate, double falseAlarmRate) {
        double pAlert = hitRate * prior + falseAlarmRate * (1 - prior);
        return hitRate * prior / pAlert;
    }

    public static void main(String[] args) {
        double prior = 0.001;          // P(outage): 1 minute in 1,000
        double hitRate = 0.99;         // P(alert | outage)
        double falseAlarm = 0.01;      // P(alert | normal minute)

        System.out.printf("P(outage | alert) = %.4f%n", pOutageGivenAlert(prior, hitRate, falseAlarm));

        // Natural frequencies: the same calculation, counted out over 100,000 minutes
        double outageMinutes = 100_000 * prior;                    // 100
        double realAlerts = outageMinutes * hitRate;               // 99
        double falseAlerts = (100_000 - outageMinutes) * falseAlarm;   // 999
        System.out.printf("per 100,000 minutes: %.0f real + %.0f false alerts, so %.0f / %.0f = %.4f%n",
            realAlerts, falseAlerts, realAlerts, realAlerts + falseAlerts, realAlerts / (realAlerts + falseAlerts));

        // Simulate ten million minutes and simply count
        Random rnd = new Random(48);
        int alerts = 0;
        int alertsDuringOutage = 0;
        for (int i = 0; i < 10_000_000; i++) {
            boolean outage = rnd.nextDouble() < prior;
            boolean alert = rnd.nextDouble() < (outage ? hitRate : falseAlarm);
            if (alert) {
                alerts++;
                if (outage) alertsDuringOutage++;
            }
        }
        System.out.printf("simulated: %,d of %,d alerts were real = %.4f%n",
            alertsDuringOutage, alerts, (double) alertsDuringOutage / alerts);

        // Independent would mean P(outage and alert) == P(outage) * P(alert)
        double pAlert = hitRate * prior + falseAlarm * (1 - prior);
        System.out.printf("P(outage and alert) = %.6f   P(outage) * P(alert) = %.6f%n", hitRate * prior, prior * pAlert);

        System.out.println("prior         hit    false alarm   P(outage | alert)");
        double[][] cases = {{0.0001, 0.99, 0.01}, {0.001, 0.99, 0.01}, {0.01, 0.99, 0.01},
                            {0.001, 0.999, 0.01}, {0.001, 0.99, 0.001}};
        for (double[] c : cases) {
            System.out.printf("1 in %-7s  %-5s  %-11s   %.4f%n", String.format("%,d", Math.round(1 / c[0])), c[1], c[2], pOutageGivenAlert(c[0], c[1], c[2]));
        }

        // A Bloom filter never misses a present key, wrongly says "maybe" for 2% of absent keys,
        // and only 1% of the keys we look up are really present.
        System.out.printf("Bloom filter: P(present | maybe) = %.4f%n", pOutageGivenAlert(0.01, 1.0, 0.02));
    }
}
