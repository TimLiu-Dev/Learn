public class Evidence {
    static boolean isPrime(long n) {
        if (n < 2) return false;
        for (long d = 2; d * d <= n; d++) if (n % d == 0) return false;
        return true;
    }

    public static void main(String[] args) {
        // Claim A: 1 + 2 + ... + n == n(n+1)/2. Checked for n up to 100,000.
        long sum = 0;
        boolean allMatch = true;
        for (long n = 1; n <= 100_000; n++) { sum += n; if (sum != n * (n + 1) / 2) allMatch = false; }
        System.out.println("Claim A holds for n = 1..100000: " + allMatch);

        // Claim B: n*n + n + 41 is prime for every n >= 0. Check until it breaks.
        long n = 0;
        while (isPrime(n * n + n + 41)) n++;
        System.out.println("Claim B holds for n = 0.." + (n - 1) + ", then fails: p(" + n + ") = " + (n * n + n + 41) + " = 41 * 41");
    }
}
