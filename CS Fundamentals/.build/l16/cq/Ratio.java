public class Ratio {
    static long steps(int n) {
        long count = 0;
        for (int i = 0; i < n; i++)
            for (int k = n; k > 1; k /= 2) count++;   // a halving loop inside a linear one
        return count;
    }

    public static void main(String[] args) {
        System.out.printf("%.2f%n", (double) steps(2048) / steps(1024));
    }
}
