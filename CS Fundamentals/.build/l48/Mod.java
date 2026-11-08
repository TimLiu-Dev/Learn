import java.math.BigInteger;

public class Mod {
    // Square-and-multiply: about 2 * log2(exp) multiplications instead of exp of them
    static long powMod(long base, long exp, long m) {
        long result = 1, b = base % m, e = exp;
        while (e > 0) {
            if (e % 2 == 1) result = result * b % m;   // low bit of e is 1: multiply it in
            b = b * b % m;                             // next bit is worth the square
            e /= 2;
        }
        return result;
    }

    public static void main(String[] args) {
        // Clock arithmetic: Wednesday (2) minus 10 days, with Monday = 0
        System.out.println("(2 - 10) % 7 = " + (2 - 10) % 7 + "   Math.floorMod(2 - 10, 7) = " + Math.floorMod(2 - 10, 7));

        // A negative hashCode breaks a naive bucket index
        int k = "android".hashCode();
        System.out.println("\"android\".hashCode() = " + k);
        System.out.println("  % 10 = " + k % 10 + "   floorMod(k, 10) = " + Math.floorMod(k, 10) + "   & 15 = " + (k & 15) + "   floorMod(k, 16) = " + Math.floorMod(k, 16));
        // ...and abs() doesn't save you (L2): one hashCode has no positive twin
        int h = "polygenelubricants".hashCode();
        System.out.println("\"polygenelubricants\".hashCode() = " + h + "   abs(h) % 10 = " + Math.abs(h) % 10);

        // Modular exponentiation
        long m = 1_000_000_007L;
        BigInteger bm = BigInteger.valueOf(m);
        System.out.println("3^200 mod m  = " + powMod(3, 200, m) + "   BigInteger: " + BigInteger.valueOf(3).modPow(BigInteger.valueOf(200), bm));
        System.out.println("3^(10^18) mod m = " + powMod(3, 1_000_000_000_000_000_000L, m) + "   BigInteger: "
                + BigInteger.valueOf(3).modPow(BigInteger.TEN.pow(18), bm));
    }
}
