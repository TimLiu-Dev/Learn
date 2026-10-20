public class Rect {
    final int width;
    final int height;

    Rect(int width, int height) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("sides must be positive");
        this.width = width;
        this.height = height;
    }

    Rect(int side) {
        this(side, side);
    }

    public static void main(String[] args) {
        Rect r = new Rect(3, 4);
        Rect sq = new Rect(5);
        System.out.println(r.width + "x" + r.height);
        System.out.println(sq.width + "x" + sq.height);
        try {
            new Rect(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }
    }
}
