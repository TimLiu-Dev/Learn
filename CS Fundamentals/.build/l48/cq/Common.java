import java.util.*;

public class Common {
    public static void main(String[] args) {
        Set<Integer> a = new TreeSet<>(Set.of(1, 2, 3, 4));
        Set<Integer> b = Set.of(3, 4, 5);
        Set<Integer> both = a;
        both.retainAll(b);
        System.out.println(both + " " + a);
    }
}
