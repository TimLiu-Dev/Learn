import java.util.List;

// Three days of traffic, one minute at a time: 1,000 requests a minute against a 99.9% SLO.
// Three quiet days of history come first, so even the 3-day window is full from the start.
public class BurnRate {
    static final int PER_MINUTE = 1000;
    static final double SLO = 0.999;
    static final double BUDGET = 1 - SLO;           // 0.1% of requests may fail
    static final int DAY = 24 * 60;
    static final int HISTORY = 3 * DAY;

    static double errorRateAt(int m) {
        int t = m - HISTORY;
        if (t >= 10 * 60 && t < 10 * 60 + 10) return 0.20;   // day 1, 10:00: a bad deploy, rolled back at 10:10
        if (t == 23 * 60) return 0.30;                         // day 1, 23:00: a one-minute blip
        if (t >= DAY && t < DAY + 12 * 60) return 0.008;       // day 2, 00:00-12:00: a slow leak
        return 0.0001;                                         // normal background errors
    }

    static final class Alert {
        final String name; final int longMin, shortMin; final double burn;
        boolean firing = false;
        Alert(String name, int longMin, int shortMin, double burn) {
            this.name = name; this.longMin = longMin; this.shortMin = shortMin; this.burn = burn;
        }
    }

    static long[] errors;                            // prefix sums (L50): errors[i] = errors in minutes before i

    // Error rate over the last w minutes, ending at minute m.
    static double rate(int m, int w) {
        int from = Math.max(0, m + 1 - w);
        return (double) (errors[m + 1] - errors[from]) / ((long) (m + 1 - from) * PER_MINUTE);
    }

    static String clock(int m) {
        return "day %d %02d:%02d".formatted((m - HISTORY) / DAY + 1, m % DAY / 60, m % 60);
    }

    public static void main(String[] args) {
        int minutes = 3 * DAY;
        errors = new long[HISTORY + minutes + 1];
        double carry = 0.0;
        for (int m = 0; m < HISTORY + minutes; m++) {
            carry += errorRateAt(m) * PER_MINUTE;
            long e = (long) carry; carry -= e;
            errors[m + 1] = errors[m] + e;
        }

        List<Alert> alerts = List.of(
            new Alert("PAGE   1h & 5m  > 14.4x", 60, 5, 14.4),
            new Alert("PAGE   6h & 30m > 6x", 360, 30, 6.0),
            new Alert("TICKET 3d & 6h  > 1x", 3 * DAY, 360, 1.0));
        int naiveFirings = 0;
        boolean naiveOn = false;

        for (int m = HISTORY; m < HISTORY + minutes; m++) {
            for (Alert a : alerts) {
                boolean on = rate(m, a.longMin) > a.burn * BUDGET && rate(m, a.shortMin) > a.burn * BUDGET;
                if (on != a.firing) {
                    a.firing = on;
                    String why = on ? "  (burn rate over %s: %.1f)".formatted(a.name.substring(7, 9).trim(), rate(m, a.longMin) / BUDGET) : "";
                    System.out.println(clock(m) + "  " + a.name + "  " + (on ? "fires" : "resolves") + why);
                }
            }
            boolean naive = rate(m, 5) > 0.01;      // "page if the 5-minute error rate is above 1%"
            if (naive && !naiveOn) naiveFirings++;
            naiveOn = naive;
        }

        double budget30d = 30 * DAY * PER_MINUTE * BUDGET;
        int h = HISTORY;
        System.out.println();
        System.out.println("error budget for 30 days: %.0f failed requests".formatted(budget30d));
        System.out.println("bad deploy  spent %5.1f%% of it".formatted(spent(h + 600, h + 610, budget30d)));
        System.out.println("blip        spent %5.1f%%".formatted(spent(h + 1380, h + 1381, budget30d)));
        System.out.println("slow leak   spent %5.1f%%".formatted(spent(h + DAY, h + DAY + 720, budget30d)));
        System.out.println("all 3 days  spent %5.1f%%".formatted(spent(h, h + minutes, budget30d)));
        System.out.println("the naive 'error rate > 1% over 5 min' alert fired " + naiveFirings + " times");
    }

    static double spent(int from, int to, double budget) { return 100 * (errors[to] - errors[from]) / budget; }
}
