import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Overload {
    static final class Point {
        final int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }

        public boolean equals(Point p) {              // no @Override
            return p != null && x == p.x && y == p.y;
        }

        @Override public int hashCode() { return Objects.hash(x, y); }
    }

    public static void main(String[] args) {
        Point a = new Point(1, 2);
        Point b = new Point(1, 2);
        Set<Point> set = new HashSet<>();
        set.add(a);
        System.out.println(a.equals(b) + " " + set.contains(b));
    }
}
