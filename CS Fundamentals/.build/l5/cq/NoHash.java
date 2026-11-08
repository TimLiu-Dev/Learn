import java.util.HashSet;
import java.util.Set;

public class NoHash {
    static class Point {
        final int x;
        Point(int x) { this.x = x; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) return false;
            return ((Point) o).x == x;
        }
        // no hashCode() override
    }

    public static void main(String[] args) {
        Set<Point> seen = new HashSet<>();
        seen.add(new Point(1));
        System.out.println(seen.contains(new Point(1)));
    }
}
