import java.lang.management.ManagementFactory;

public class HotHunt {
    static int collatzSteps(long n) {
        int steps = 0;
        while (n != 1) {
            n = (n % 2 == 0) ? n / 2 : 3 * n + 1;
            steps++;
        }
        return steps;
    }

    public static void main(String[] args) {
        var uptime = ManagementFactory.getRuntimeMXBean();
        long sink = 0;
        for (int batch = 1; batch <= 8; batch++) {
            long t0 = System.nanoTime();
            for (long n = 1; n <= 2_000; n++) sink += collatzSteps(n);
            long micros = (System.nanoTime() - t0) / 1_000;
            System.out.printf("batch %d ends at %3d ms: %5d µs%n", batch, uptime.getUptime(), micros);
        }
        System.out.println("sink=" + sink);
    }
}
