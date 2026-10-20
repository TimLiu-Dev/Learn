import java.util.List;

// Give the Big O of each method in terms of n (the input size). m = size of the second input.
public class Ex1 {
    static int last(List<Integer> xs) { return xs.get(xs.size() - 1); }   // (a)

    static int countDigits(int n) {                                       // (b) n is the number itself
        int digits = 1;
        for (int k = n; k >= 10; k /= 10) digits++;
        return digits;
    }

    static boolean hasPairSum(int[] xs, int target) {                     // (c)
        for (int i = 0; i < xs.length; i++)
            for (int j = i + 1; j < xs.length; j++)
                if (xs[i] + xs[j] == target) return true;
        return false;
    }

    static int[] sumAndMax(int[] xs) {                                    // (d)
        int sum = 0;
        for (int x : xs) sum += x;
        int max = Integer.MIN_VALUE;
        for (int x : xs) if (x > max) max = x;
        return new int[] {sum, max};
    }

    static int commonCount(List<String> a, List<String> b) {             // (e)
        int count = 0;
        for (String s : a) if (b.contains(s)) count++;
        return count;
    }

    static int triangle(int n) {                                          // (f)
        int stars = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j <= i; j++) stars++;
        return stars;
    }

    static int firstNegative(int[] xs) {                                  // (g) best case? worst case?
        for (int i = 0; i < xs.length; i++) if (xs[i] < 0) return i;
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(last(List.of(3, 1, 4)));
        System.out.println(countDigits(90210));
        System.out.println(hasPairSum(new int[] {3, 8, 5}, 13));
        System.out.println(java.util.Arrays.toString(sumAndMax(new int[] {3, 8, 5})));
        System.out.println(commonCount(List.of("a", "b", "c"), List.of("b", "c", "d")));
        System.out.println(triangle(4));
        System.out.println(firstNegative(new int[] {-2, 5, 7}));
    }
}
