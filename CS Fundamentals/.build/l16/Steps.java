// Count the steps instead of timing them: the count depends only on n.
public class Steps {
    static long constant(int n) { return 1; }      // one step, whatever n is

    static long linear(int n) {
        long steps = 0;
        for (int i = 0; i < n; i++) steps++;        // n steps
        return steps;
    }

    static long nested(int n) {
        long steps = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) steps++;    // n * n steps
        return steps;
    }

    static long halving(int n) {
        long steps = 0;
        for (int k = n; k > 1; k /= 2) steps++;     // how many times can n be halved?
        return steps;
    }

    public static void main(String[] args) {
        System.out.printf("%8s %6s %8s %10s %14s%n", "n", "O(1)", "O(log n)", "O(n)", "O(n^2)");
        for (int n : new int[] {10, 100, 1_000, 10_000, 100_000}) {
            System.out.printf("%8d %6d %8d %10d %14d%n", n, constant(n), halving(n), linear(n), nested(n));
        }
    }
}
