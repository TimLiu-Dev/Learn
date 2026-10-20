import java.util.stream.IntStream;

public class Primes {
    static boolean isPrime(int n) {
        return n >= 2 && IntStream.rangeClosed(2, n / 2).noneMatch(d -> n % d == 0);
    }

    static IntStream primes() {                                // a NEW stream on every call
        return IntStream.iterate(2, n -> n + 1).filter(Primes::isPrime);
    }

    public static void main(String[] args) {
        System.out.println(primes().limit(10).boxed().toList());
        System.out.println(primes().filter(p -> p > 1_000).findFirst().getAsInt());
        System.out.println(primes().takeWhile(p -> p < 50).count());
    }
}
