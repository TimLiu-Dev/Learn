import java.util.List;

public class Recursion {
    static long factorial(int n) {
        return n <= 1 ? 1 : n * factorial(n - 1);
    }

    // sum of a list = first element + sum of the rest
    static int sum(List<Integer> xs) {
        if (xs.isEmpty()) return 0;
        return xs.get(0) + sum(xs.subList(1, xs.size()));
    }

    // reverse of a string = reverse of the rest + first char
    static String reverse(String s) {
        if (s.length() <= 1) return s;
        return reverse(s.substring(1)) + s.charAt(0);
    }

    public static void main(String[] args) {
        System.out.println(factorial(5));
        System.out.println(factorial(20));
        System.out.println(sum(List.of(3, 1, 4, 1, 5)));
        System.out.println(sum(List.of()));
        System.out.println(reverse("stressed"));
    }
}
