// Exercise: how far apart can two NTP-synced clocks get?
// Clock A runs 50 ppm fast, clock B 50 ppm slow; both are reset to true time every syncSec seconds.
public class DriftBound {
    static double worstSkewMs(int syncSec, int hours, double ppm) {
        double a = 0, b = 0;                          // each clock's error, in ms
        double worst = 0;
        for (int ms = 1; ms <= hours * 3_600_000; ms++) {
            a += ppm / 1_000_000;                     // 50 ppm = 0.00005 ms gained per real ms
            b -= ppm / 1_000_000;
            worst = Math.max(worst, a - b);
            if (ms % (syncSec * 1_000) == 0) { a = 0; b = 0; }   // sync: both back to true time
        }
        return worst;
    }

    public static void main(String[] args) {
        System.out.println(String.format("%-10s %12s %12s", "sync every", "simulated", "2 x ppm x T"));
        for (int s : new int[]{16, 64, 1024}) {
            System.out.println(String.format("%-10s %9.1f ms %9.1f ms", s + " s", worstSkewMs(s, 2, 50.0), 2 * 50e-6 * s * 1000));
        }
    }
}
