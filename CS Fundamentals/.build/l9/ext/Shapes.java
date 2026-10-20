public class Shapes {
    static class Shape {
        String kind() { return "shape"; }          // member: dispatched at RUNTIME
    }

    static class Circle extends Shape {
        @Override String kind() { return "circle"; }
    }

    static String name(Shape s) { return "Shape"; }    // static overloads: chosen at COMPILE time
    static String name(Circle c) { return "Circle"; }

    public static void main(String[] args) {
        Circle c = new Circle();
        System.out.println(name(c) + " / " + c.kind());

        Shape s = new Circle();
        System.out.println(name(s) + " / " + s.kind());
    }
}
