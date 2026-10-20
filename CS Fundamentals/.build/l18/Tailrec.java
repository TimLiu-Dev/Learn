public class Tailrec {
    // Same tail-call shape, but Java has no tailrec: every call still pushes a frame.
    static long sumTo(long n, long acc) {
        return n == 0 ? acc : sumTo(n - 1, acc + n);
    }

    public static void main(String[] args) {
        System.out.println(sumTo(1_000_000, 0));
    }
}
