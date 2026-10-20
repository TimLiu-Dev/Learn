package katas;

public class Points {
    public static class PlainPoint {               // equals = identity (L5)
        final int x, y;
        public PlainPoint(int x, int y) { this.x = x; this.y = y; }
    }

    public record DataPoint(int x, int y) {}       // equals = contents (L7)
}
