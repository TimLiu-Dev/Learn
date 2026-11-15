import java.util.ArrayList;
import java.util.List;

public class SameRows {
    record Row(String name, int books) {}

    public static void main(String[] args) {
        List<Row> slow = new ArrayList<>();          // built row by row, like the N+1 loop
        slow.add(new Row("Ted Chiang", 2));
        List<Row> fast = List.of(new Row("Ted Chiang", 2));   // from the one JOIN query
        System.out.println((slow == fast) + " " + slow.equals(fast));
    }
}
