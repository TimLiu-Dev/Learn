// Back-of-envelope estimates for a URL shortener: every number comes from a stated assumption.
public class Estimates {
    static final long SECONDS_PER_MONTH = 30L * 24 * 3600;     // 2,592,000 ≈ "2.5 million"

    static String human(double n) {
        if (n >= 1e12) return "%.1f T".formatted(n / 1e12);
        if (n >= 1e9) return "%.1f G".formatted(n / 1e9);
        if (n >= 1e6) return "%.1f M".formatted(n / 1e6);
        if (n >= 1e3) return "%.1f k".formatted(n / 1e3);
        return "%.1f".formatted(n);
    }

    public static void main(String[] args) {
        // Assumptions (say them out loud: change one and every number below moves)
        double newLinksPerMonth = 100e6;
        double readsPerWrite = 100.0;
        double peakFactor = 3.0;
        double bytesPerLinkRow = 500.0;     // code + long URL (~100 B) + timestamps + index overhead, rounded up
        int years = 5;
        double hotFraction = 0.2;           // the hot 20% of last month's links serve most reads
        double bytesPerRedirectResponse = 500.0;   // status line + headers, no body

        double writesPerSec = newLinksPerMonth / SECONDS_PER_MONTH;
        double readsPerSec = writesPerSec * readsPerWrite;
        System.out.println("writes/s: avg " + human(writesPerSec) + ", peak " + human(writesPerSec * peakFactor));
        System.out.println("reads/s:  avg " + human(readsPerSec) + ", peak " + human(readsPerSec * peakFactor));

        double totalLinks = newLinksPerMonth * 12 * years;
        System.out.println("links after " + years + " years: " + human(totalLinks));
        System.out.println("link storage: " + human(totalLinks * bytesPerLinkRow) + "B");

        double rawClicks = readsPerSec * SECONDS_PER_MONTH * 12 * years;
        System.out.println("raw click rows after " + years + " years: " + human(rawClicks)
                + " (x 50 B = " + human(rawClicks * 50) + "B)");

        System.out.println("cache for hot links: " + human(hotFraction * newLinksPerMonth * bytesPerLinkRow) + "B");
        System.out.println("redirect bandwidth: avg " + human(readsPerSec * bytesPerRedirectResponse) + "B/s, "
                + "peak " + human(readsPerSec * peakFactor * bytesPerRedirectResponse) + "B/s");

        int minChars = (int) Math.ceil(Math.log(totalLinks) / Math.log(62));
        System.out.println("base62 chars needed for " + human(totalLinks) + " codes: " + minChars
                + " (62^" + minChars + " = " + human(Math.pow(62, minChars)) + ")");

        // From rates to machines with Little's law (L28): in flight = arrival rate x time inside.
        double serverSecPerRedirect = 0.002;   // budget: a cache hit is well under 1 ms, a miss adds a DB round trip
        double missRate = 0.05;                // Part 2's prototype measured a 95.7% hit rate
        double dbSecPerMiss = 0.001;           // one indexed lookup across the network
        double redirectsPerServer = 4_000.0;   // a guess until a load test on real hardware replaces it
        double peakReads = readsPerSec * peakFactor;
        System.out.println("in flight at peak: " + human(peakReads * serverSecPerRedirect) + " requests");
        System.out.println("DB connections busy at peak: " + human(peakReads * missRate * dbSecPerMiss));
        int servers = (int) Math.ceil(peakReads / redirectsPerServer);
        System.out.println("app servers for peak: " + servers + ", plus 1 spare = " + (servers + 1));
    }
}
