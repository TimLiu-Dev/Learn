public class Factorial {
    static long factorial(int n) {
        if (n <= 1) return 1;            // base case: stop here
        return n * factorial(n - 1);     // recursive case: a smaller problem
    }

    public static void main(String[] args) {
        long f = factorial(4);
        System.out.println(f);
    }
}
