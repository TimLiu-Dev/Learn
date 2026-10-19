import java.util.Arrays;
import java.util.List;

public class Equality {
    static class Box {                   // no equals() of its own
        final int value;
        Box(int value) { this.value = value; }
    }

    public static void main(String[] args) {
        Box b1 = new Box(1);
        Box b2 = new Box(1);
        Box b3 = b1;
        System.out.println(b1.equals(b2) + " " + (b1 == b2) + " " + (b1 == b3));

        String s1 = "hi";
        String s2 = new StringBuilder("h").append("i").toString();
        System.out.println(s1.equals(s2) + " " + (s1 == s2));

        List<Integer> l1 = List.of(1, 2);
        List<Integer> l2 = List.of(1, 2);
        System.out.println(l1.equals(l2) + " " + (l1 == l2));

        int[] a1 = {1, 2};
        int[] a2 = {1, 2};
        System.out.println(a1.equals(a2) + " " + Arrays.equals(a1, a2));

        String lit1 = "hi";
        String lit2 = "hi";
        System.out.println(lit1 == lit2);   // true: identical literals share one interned object
    }
}
