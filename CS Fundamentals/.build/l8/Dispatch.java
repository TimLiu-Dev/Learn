import java.util.List;

public class Dispatch {
    interface Shape {
        double area();
    }

    record Circle(double r) implements Shape {
        public double area() { return Math.PI * r * r; }
    }

    record Rect(double w, double h) implements Shape {
        public double area() { return w * h; }
    }

    record Triangle(double base, double height) implements Shape {
        public double area() { return base * height / 2; }
    }

    static double totalArea(List<Shape> shapes) {   // knows nothing about circles or rectangles
        double total = 0;
        for (Shape s : shapes) total += s.area();   // which area()? decided per object, at runtime
        return total;
    }

    public static void main(String[] args) {
        List<Shape> shapes = List.of(new Circle(1.0), new Rect(2.0, 3.0), new Triangle(4.0, 5.0));
        for (Shape s : shapes) System.out.println(s + " -> " + String.format("%.2f", s.area()));
        System.out.println("total = " + String.format("%.2f", totalArea(shapes)));
    }
}
