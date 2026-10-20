record Point(int x, int y) {}

public class ClassObjects {
    public static void main(String[] args) throws Exception {
        Point p = new Point(1, 2);
        Class<Point> c1 = Point.class;                 // class literal
        Class<? extends Point> c2 = p.getClass();      // from an instance
        Class<?> c3 = Class.forName("Point");          // by name, found at runtime

        System.out.println(c1);
        System.out.println("same Class: " + (c1 == c2 && c2 == c3));
        System.out.println("names: " + c1.getSimpleName() + " " + c1.getName());
        System.out.println("record? " + c1.isRecord() + ", super: " + c1.getSuperclass());
    }
}
