public class Logs {
    // How many times can you halve n before reaching 1?
    static int halvings(int n) {
        int count = 0;
        while (n > 1) { n /= 2; count++; }
        return count;
    }

    public static void main(String[] args) {
        System.out.println("n              halvings  31-leadingZeros  floor(log2)");
        for (int n : new int[]{1, 8, 1000, 1_000_000, 1_000_000_000, Integer.MAX_VALUE}) {
            double log2 = Math.log(n) / Math.log(2);             // Java has no log2 for doubles
            System.out.printf("%-14d %8d  %15d  %11.0f%n", n, halvings(n), 31 - Integer.numberOfLeadingZeros(n), Math.floor(log2));
        }
        // The floating-point trap from L2: change of base is not exact
        System.out.println("ln(1000)/ln(10) = " + Math.log(1000) / Math.log(10));
    }
}
