public class Trace {
    static int c() {
        throw new IllegalStateException("boom");
    }

    static int b() { return c() * 2; }

    static int a() { return b() + 1; }

    public static void main(String[] args) {
        System.out.println(a());
    }
}
