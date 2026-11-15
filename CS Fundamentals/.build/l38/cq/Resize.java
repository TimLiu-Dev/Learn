public class Resize {
    static class Rectangle {
        protected int width, height;
        Rectangle(int width, int height) { this.width = width; this.height = height; }
        void setWidth(int w) { width = w; }
        void setHeight(int h) { height = h; }
        int area() { return width * height; }
    }

    static class Square extends Rectangle {
        Square(int side) { super(side, side); }
        @Override void setWidth(int w) { width = w; height = w; }
        @Override void setHeight(int h) { width = h; height = h; }
    }

    // Written against Rectangle: "make it 5 by 2".
    static void resize(Rectangle r) {
        r.setWidth(5);
        r.setHeight(2);
    }

    public static void main(String[] args) {
        Rectangle r = new Square(3);
        resize(r);
        System.out.println(r.area());
    }
}
