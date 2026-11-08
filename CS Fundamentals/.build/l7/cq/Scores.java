import java.util.List;

public class Scores {
    record Team(String name, int[] points) {}
    record Squad(String name, List<Integer> points) {}

    public static void main(String[] args) {
        Team a = new Team("red", new int[] {1, 2});
        Team b = new Team("red", new int[] {1, 2});
        Squad c = new Squad("red", List.of(1, 2));
        Squad d = new Squad("red", List.of(1, 2));
        System.out.println(a.equals(b) + " " + c.equals(d));
    }
}
