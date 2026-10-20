public class Calc {
    static int add(int a, int b) {
        return a + b;
    }

    static int sumTo(int n) {
        int total = 0;
        for (int i = 1; i <= n; i++) {
            total += i;
        }
        return total;
    }

    public static void main(String[] args) {
        System.out.println(add(2, 3) + " " + sumTo(4));
    }
}
