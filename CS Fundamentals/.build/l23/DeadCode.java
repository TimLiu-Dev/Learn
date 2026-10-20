public class DeadCode {
    static double x = Math.PI;
    static void wrong() { Math.log(x); }              // result thrown away
    static double right() { return Math.log(x); }     // result returned and used

    public static void main(String[] args) {
        double sink = 0;
        for (int round = 1; round <= 5; round++) {
            long t0 = System.nanoTime();
            for (int i = 0; i < 10_000_000; i++) wrong();
            long t1 = System.nanoTime();
            for (int i = 0; i < 10_000_000; i++) sink += right();
            long t2 = System.nanoTime();
            System.out.printf("round %d: discarded %5d µs, used %6d µs%n",
                    round, (t1 - t0) / 1_000, (t2 - t1) / 1_000);
        }
        System.out.println("sink=" + sink);
    }
}
