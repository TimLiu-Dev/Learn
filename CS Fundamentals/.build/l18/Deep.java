public class Deep {
    static int depth = 0;

    static void dive() {                 // no base case at all
        depth++;
        dive();
    }

    static long sumTo(long n) {
        return n == 0 ? 0 : n + sumTo(n - 1);
    }

    static long sumToLoop(long n) {
        long total = 0;
        for (long i = 1; i <= n; i++) total += i;
        return total;
    }

    public static void main(String[] args) {
        try {
            dive();
        } catch (StackOverflowError e) {         // demo only: never catch this in real code
            System.out.println("dive(): StackOverflowError after " + depth + " frames");
        }
        System.out.println("sumTo(10_000) = " + sumTo(10_000));
        try {
            System.out.println(sumTo(1_000_000));
        } catch (StackOverflowError e) {
            System.out.println("sumTo(1_000_000): StackOverflowError");
        }
        System.out.println("sumToLoop(1_000_000) = " + sumToLoop(1_000_000));
    }
}
