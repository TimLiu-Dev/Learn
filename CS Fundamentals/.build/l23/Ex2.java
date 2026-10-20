public class Ex2 {
    static class Limits {
        static { System.out.println("  [Limits initialised]"); }
        static final int MAX = 50;                     // compile-time constant
        static final String label = "lim".concat("its"); // computed at runtime
    }

    public static void main(String[] args) {
        System.out.println("A: " + Limits.MAX);
        System.out.println("B: " + Limits.label);
        System.out.println("C: " + Limits.MAX);
    }
}
