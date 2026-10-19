public class FactorialFixed {
    public static void main(String[] args) {
        // Fix 1: a bigger type
        long big = 1L;
        for (int n = 1; n <= 20; n++) big *= n;
        System.out.println("20! = " + big);

        // Fix 2: fail loudly instead of silently wrapping
        int f = 1;
        for (int n = 1; n <= 15; n++) {
            f = Math.multiplyExact(f, n);
            System.out.println(n + "! = " + f);
        }
    }
}
