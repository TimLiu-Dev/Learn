import java.util.List;

public class Interfaces {
    interface Shape {
        String name();                       // abstract: implementers must provide it
        double area();                       // abstract
        default String describe() {          // a default method WITH a body
            return name() + " with area " + String.format("%.2f", area());
        }
    }

    interface Scalable {
        Shape scaled(double factor);
    }

    static class Circle implements Shape, Scalable {   // one class, two interfaces
        private final double radius;
        Circle(double radius) { this.radius = radius; }
        public String name() { return "circle"; }      // interface methods are public
        public double area() { return Math.PI * radius * radius; }
        public Circle scaled(double factor) { return new Circle(radius * factor); }
    }

    static class Square implements Shape, Scalable {
        private final double side;
        Square(double side) { this.side = side; }
        public String name() { return "square"; }
        public double area() { return side * side; }
        public Square scaled(double factor) { return new Square(side * factor); }
        @Override
        public String describe() { return "a " + side + "x" + side + " square"; }   // replaces the default
    }

    public static void main(String[] args) {
        List<Shape> shapes = List.of(new Circle(1.0), new Square(2.0));
        for (Shape s : shapes) System.out.println(s.describe());
        System.out.println(new Square(2.0).scaled(3.0).describe());
    }
}
