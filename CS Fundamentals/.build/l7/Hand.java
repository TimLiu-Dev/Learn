import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Hand {
    static final class Point {
        private final int x;
        private final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int x() { return x; }
        int y() { return y; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;                   // same object: trivially equal
            if (!(o instanceof Point other)) return false; // null or a different type
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);                    // equal points -> equal hash codes
        }

        @Override
        public String toString() {
            return "Point(x=" + x + ", y=" + y + ")";
        }
    }

    public static void main(String[] args) {
        Point a = new Point(1, 2);
        Point b = new Point(1, 2);
        System.out.println(a.equals(b));
        System.out.println(a == b);
        System.out.println(a.hashCode() == b.hashCode());
        System.out.println(a);
        Set<Point> set = new HashSet<>();
        set.add(a);
        System.out.println(set.contains(b));
    }
}
