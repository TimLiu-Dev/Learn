package katas;

public class App {
    public static void main(String[] args) {
        for (int n : new int[]{0, 15, 16, 1_000_000, Integer.MAX_VALUE}) System.out.println("isqrt(" + n + ") = " + Proofs.isqrt(n));
        System.out.println("2^10 mod 1000 = " + Proofs.powMod(2, 10, 1000));
        System.out.println("3^(10^18) mod 1000000007 = " + Proofs.powMod(3, 1_000_000_000_000_000_000L, 1_000_000_007));
    }
}
