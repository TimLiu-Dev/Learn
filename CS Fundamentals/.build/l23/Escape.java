import java.lang.management.ManagementFactory;

public class Escape {
    record Point(int x, int y) {}

    static long sumPoints(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            Point p = new Point(i, i + 1);     // never leaves this method
            s += p.x() + p.y();
        }
        return s;
    }

    public static void main(String[] args) {
        var mx = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        long sink = 0;
        for (int round = 1; round <= 5; round++) {
            long before = mx.getCurrentThreadAllocatedBytes();
            sink += sumPoints(1_000_000);
            long after = mx.getCurrentThreadAllocatedBytes();
            System.out.printf("round %d: %,d bytes allocated%n", round, after - before);
        }
        System.out.println("sink=" + sink);
    }
}
