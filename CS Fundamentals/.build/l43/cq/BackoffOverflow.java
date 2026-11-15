public class BackoffOverflow {
    static int backoffMs(int attempt) {
        int base = 100, cap = 10_000;
        return Math.min(cap, base * (1 << attempt));
    }

    public static void main(String[] args) {
        System.out.println(backoffMs(3) + " " + backoffMs(25) + " " + backoffMs(32));
    }
}
