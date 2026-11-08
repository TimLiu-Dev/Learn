import java.math.BigInteger;

public class Counting {
    static long factorial(int n) {
        long result = 1;
        for (int k = 1; k <= n; k++) result *= k;
        return result;
    }

    // C(n, k) without factorials: after step i, result == C(n, i + 1), so every division is exact.
    static long choose(int n, int k) {
        long result = 1;
        for (int i = 0; i < k; i++) result = result * (n - i) / (i + 1);
        return result;
    }

    public static void main(String[] args) {
        // Product rule: independent choices multiply.
        System.out.println("4-digit PINs:        " + String.format("%,d", 10L * 10 * 10 * 10));
        System.out.println("8 lowercase letters: " + String.format("%,d", Math.powExact(26L, 8)));
        long wrapped = 1;
        for (int i = 0; i < 12; i++) wrapped *= 62;
        System.out.println("12 chars of [a-zA-Z0-9] as long: " + wrapped + "   <- overflowed");
        System.out.println("12 chars as BigInteger:          " + BigInteger.valueOf(62).pow(12));

        // Permutations and combinations
        System.out.println("20! = " + factorial(20));
        System.out.println("21! = " + factorial(21) + "   <- overflowed");
        System.out.println("podium orders of 10 runners P(10,3) = " + (10 * 9 * 8) + ", teams of 3 C(10,3) = " + choose(10, 3));
        System.out.println("poker hands C(52,5) = " + String.format("%,d", choose(52, 5)));
        BigInteger big = BigInteger.ONE;
        for (int i = 1; i <= 30; i++) big = big.multiply(BigInteger.valueOf(60L - i + 1)).divide(BigInteger.valueOf(i));
        System.out.println("C(60,30) = " + choose(60, 30) + ", BigInteger check: " + big);

        // Pigeonhole: more strings than int values means some must share a hashCode
        System.out.println("\"Aa\".hashCode() = " + "Aa".hashCode() + ", \"BB\".hashCode() = " + "BB".hashCode());
    }
}
