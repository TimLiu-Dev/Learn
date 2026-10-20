import java.util.Objects;

public class StringHash {
    record Point(int x, int y) {}

    static final class Pixel {                      // a plain class: you write hashCode yourself
        final int x, y;
        Pixel(int x, int y) { this.x = x; this.y = y; }
        @Override public boolean equals(Object o) {
            return o instanceof Pixel p && p.x == x && p.y == y;
        }
        @Override public int hashCode() { return Objects.hash(x, y); }
    }

    // The String.hashCode formula from the JDK docs, by hand
    static int hashByHand(String s) {
        int h = 0;
        for (char c : s.toCharArray()) h = 31 * h + c;   // int arithmetic: overflow just wraps
        return h;
    }

    public static void main(String[] args) {
        System.out.println("by hand: " + hashByHand("cat") + "  built-in: " + "cat".hashCode());
        System.out.println("c*31^2 + a*31 + t = " + (99 * 31 * 31 + 97 * 31 + 116));
        System.out.println("banana: " + "banana".hashCode());
        System.out.println("Aa=" + "Aa".hashCode() + " BB=" + "BB".hashCode());
        System.out.println(new Point(1, 2).hashCode() == new Point(1, 2).hashCode());
        System.out.println("Pixel(1, 2): " + new Pixel(1, 2).hashCode());
    }
}
