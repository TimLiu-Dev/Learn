public class Hot {
    static long work(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            s += (i ^ (i >>> 3)) % 7;
        }
        return s;
    }

    public static void main(String[] args) {
        long sink = 0;
        for (int batch = 1; batch <= 10; batch++) {
            long t0 = System.nanoTime();
            for (int r = 0; r < 20; r++) sink += work(10_000);
            long micros = (System.nanoTime() - t0) / 1_000;
            System.out.printf("batch %d: %6d µs%n", batch, micros);
        }
        System.out.println("sink=" + sink);           // use the result (L16)
    }
}
