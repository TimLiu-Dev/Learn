package shapes;

public class Shapes {
    public static class Rectangle {
        protected int width, height;
        public Rectangle(int width, int height) { this.width = width; this.height = height; }
        public void setWidth(int w) { width = w; }
        public void setHeight(int h) { height = h; }
        public int getHeight() { return height; }
        public int area() { return width * height; }
    }

    // "A square IS-A rectangle" (true in geometry) ...
    public static final class Square extends Rectangle {
        public Square(int side) { super(side, side); }
        @Override public void setWidth(int w) { width = w; height = w; }    // ... so keep the sides equal
        @Override public void setHeight(int h) { width = h; height = h; }
    }
}
