import java.math.BigInteger;

public class BigFib {
    public static void main(String[] args) {
        long prev = 0, cur = 1;
        for (int n = 1; n <= 93; n++) {
            long next = prev + cur;
            prev = cur;
            cur = next;
            if (n >= 91) System.out.println("fib(" + n + ") as Long   = " + prev);
        }

        BigInteger a = BigInteger.ZERO, b = BigInteger.ONE;
        for (int i = 0; i < 93; i++) {
            BigInteger next = a.add(b);
            a = b;
            b = next;
        }
        System.out.println("fib(93) as BigInteger = " + a);
    }
}
