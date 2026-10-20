import java.util.Collections;
import java.util.List;

public class BadBound {
    static <T extends Comparable<T>> T largest(List<T> items) { return Collections.max(items); }

    record Point(int x, int y) {}                // not Comparable

    public static void main(String[] args) {
        List<Point> points = List.of(new Point(1, 2), new Point(3, 4));
        System.out.println(BadBound.<Point>largest(points));   // T = Point breaks the bound
    }
}
