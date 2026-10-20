import java.util.Arrays;

public class Detective {
    record Money(long cents, String currency) {}

    static String shout(String s) { return s.toUpperCase(); }

    static long countEvens(int[] xs) {
        return Arrays.stream(xs).filter(x -> x % 2 == 0).count();
    }

    static String size(int code) {
        return switch (code) {
            case 0 -> "S";
            case 1 -> "M";
            case 2 -> "L";
            case 3 -> "XL";
            default -> "?";
        };
    }

    public static void main(String[] args) {
        System.out.println(shout("case closed"));
        System.out.println(countEvens(new int[] {1, 2, 3, 4, 6}));
        System.out.println(size(2) + " " + size(9));
        System.out.println(new Money(500, "EUR").equals(new Money(500, "EUR")));
    }
}
