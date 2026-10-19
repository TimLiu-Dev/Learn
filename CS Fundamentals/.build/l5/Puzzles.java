import java.util.ArrayList;
import java.util.List;

public class Puzzles {
    public static void main(String[] args) {
        Integer x = 1000, y = 1000;
        System.out.println(x == y);                          // P1

        List<Integer> first = new ArrayList<>(List.of(1, 2));
        List<Integer> second = first;
        second.add(3);
        System.out.println(first);                           // P2

        System.out.println(new int[]{1, 2}.equals(new int[]{1, 2}));   // P3

        String s = "hel";
        String t = s + "lo";
        System.out.println(t == "hello");                    // P4
        System.out.println(t.equals("hello"));               // P5
    }
}
