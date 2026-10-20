public class Order {
    static int step(int n) {
        try {
            System.out.println("try " + n);
            if (n == 2) throw new IllegalStateException("boom " + n);
            return n * 10;
        } catch (IllegalStateException e) {
            System.out.println("catch " + e.getMessage());
            return -1;
        } finally {
            System.out.println("finally " + n);
        }
    }

    public static void main(String[] args) {
        System.out.println(step(1));
        System.out.println(step(2));
    }
}
