public class Warmup {
    static long work(int n) {                 // some O(n^2) work, the same every time
        long total = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) total += (i ^ j) & 7;
        return total;
    }

    public static void main(String[] args) {
        long sink = 0;
        for (int run = 1; run <= 8; run++) {
            long t0 = System.nanoTime();
            sink += work(3_000);
            long t1 = System.nanoTime();
            System.out.println("run " + run + ": " + (t1 - t0) / 1_000 + " µs");
        }
        System.out.println("(sink " + sink + ")");
    }
}
