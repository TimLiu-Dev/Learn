import java.util.List;

public class Shapes {
    sealed interface Shape permits Circle, Rect, Square {}
    record Circle(double radius) implements Shape {}
    record Rect(double width, double height) implements Shape {}
    record Square(double side) implements Shape {}   // step 2: add this, watch area() fail, then fix it

    static double area(Shape shape) {
        return switch (shape) {
            case Circle(double r) -> Math.PI * r * r;
            case Rect(double w, double h) -> w * h;
            case Square(double side) -> side * side;
        };
    }

    public static void main(String[] args) {
        List<Shape> shapes = List.of(new Circle(1.0), new Rect(2.0, 3.0), new Square(4.0));
        for (Shape s : shapes) System.out.println(s + " -> " + String.format("%.2f", area(s)));
    }
}
