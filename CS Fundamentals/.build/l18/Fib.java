public class Fib {
    static long calls = 0;

    static long fib(int n) {
        calls++;
        return n < 2 ? n : fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] args) {
        for (int n : new int[] {10, 20, 30, 40, 45}) {
            calls = 0;
            long start = System.nanoTime();
            long f = fib(n);
            long ms = (System.nanoTime() - start) / 1_000_000;
            System.out.printf("fib(%d) = %,d   calls = %,d   %d ms%n", n, f, calls, ms);
        }
    }
}
