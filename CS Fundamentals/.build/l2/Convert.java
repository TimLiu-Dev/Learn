public class Convert {
    public static void main(String[] args) {
        int small = 2_000_000_000;
        long wide = small;                       // widening is implicit
        long sum = small + 1L;                   // int + long -> long
        int chopped = (int) 5_000_000_000L;      // narrowing needs a cast, then drops the high bits
        int truncated = (int) 3.99;              // double -> int drops the fraction
        System.out.println(wide + " " + sum + " " + chopped + " " + truncated);
    }
}
