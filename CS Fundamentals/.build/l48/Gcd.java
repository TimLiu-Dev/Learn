public class Gcd {
    // Slow but obviously correct: try every candidate divisor.
    static int gcdSlow(int a, int b) {
        for (int d = Math.max(a, b); d >= 1; d--) if (a % d == 0 && b % d == 0) return d;
        throw new IllegalStateException("unreachable");
    }

    // Euclid as a state machine: (x, y) -> (y, x % y). Invariant: gcd(x, y) == gcd(a, b).
    static int gcd(int a, int b) {
        if (a < 0 || b < 0 || (a == 0 && b == 0)) throw new IllegalArgumentException("bad input");
        int target = gcdSlow(a, b);                 // only for checking the invariant
        int x = a, y = b, steps = 0;
        while (y != 0) {
            if (gcdSlow(x, y) != target) throw new IllegalStateException("invariant broken at (" + x + ", " + y + ")");
            int r = x % y;
            x = y;
            y = r;
            steps++;
        }
        // y == 0, so gcd(x, 0) == x, and the invariant says that equals gcd(a, b).
        System.out.println("gcd(" + a + ", " + b + ") = " + x + " in " + steps + " steps");
        return x;
    }

    public static void main(String[] args) {
        gcd(1071, 462);
        gcd(462, 1071);        // first step just swaps them
        gcd(17, 0);
        gcd(832040, 514229);   // consecutive Fibonacci numbers: many steps for their size
    }
}
