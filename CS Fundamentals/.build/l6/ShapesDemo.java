public class ShapesDemo {
    public static void main(String[] args) {
        System.out.println(AppInfo.describe());
        Square a = Square.of(3);
        Square b = Square.of(4);
        System.out.println(a.getArea() + " " + b.getArea());
        System.out.println("squares created: " + Square.created());
    }
}

final class AppInfo {
    static final String VERSION = "1.4.2";

    private AppInfo() { }                       // no instances: it's just a holder

    static String describe() { return "Shapes v" + VERSION; }
}

final class Square {
    private static int created = 0;
    private final int side;

    private Square(int side) { this.side = side; }

    static Square of(int side) {
        if (side <= 0) throw new IllegalArgumentException("side must be positive");
        created++;
        return new Square(side);
    }

    static int created() { return created; }

    int getArea() { return side * side; }
}
