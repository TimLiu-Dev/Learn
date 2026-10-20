import java.util.List;

public class ExtExercise {
    static boolean isPalindrome(String s) {
        String cleaned = s.toLowerCase().replace(" ", "");
        return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
    }

    static boolean isEven(int n) {
        return n % 2 == 0;
    }

    static int sumOrZero(List<Integer> list) {
        if (list == null) return 0;
        int total = 0;
        for (int n : list) total += n;
        return total;
    }

    public static void main(String[] args) {
        System.out.println(isPalindrome("Never odd or even"));
        System.out.println(isPalindrome("Kotlin"));
        System.out.println(isEven(4));
        System.out.println(isEven(7));
        System.out.println(sumOrZero(null));
        System.out.println(sumOrZero(List.of(1, 2, 3)));
    }
}
