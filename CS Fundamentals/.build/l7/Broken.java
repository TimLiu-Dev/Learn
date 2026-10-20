import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Broken {
    static final class BadPoint {
        final int x, y;
        BadPoint(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object o) {
            return o instanceof BadPoint p && x == p.x && y == p.y;
        }
        // hashCode() NOT overridden: each object keeps its identity-based hash
    }

    public static void main(String[] args) {
        Set<BadPoint> set = new HashSet<>();
        set.add(new BadPoint(1, 2));
        System.out.println(new BadPoint(1, 2).equals(new BadPoint(1, 2)));  // equals says yes...
        System.out.println(set.contains(new BadPoint(1, 2)));               // ...the HashSet says no
        set.add(new BadPoint(1, 2));
        System.out.println(set.size());                                     // a "duplicate" got in

        Map<BadPoint, String> names = new HashMap<>();
        names.put(new BadPoint(0, 0), "origin");
        System.out.println(names.get(new BadPoint(0, 0)));                  // lookup by an equal key fails
    }
}
