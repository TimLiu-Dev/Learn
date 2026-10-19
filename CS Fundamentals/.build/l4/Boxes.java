import java.util.List;

public class Boxes {
    static class Box {
        int value;
        Box(int value) { this.value = value; }
    }

    static int grow(Box b, int n) {
        b.value += n;
        int m = n;
        m *= 10;
        return m;
    }

    public static void main(String[] args) {
        Box a = new Box(1);
        Box b = a;
        Box c = new Box(1);
        int n = 5;
        int result = grow(b, n);
        System.out.println(a.value + " " + b.value + " " + c.value + " " + n + " " + result);

        List<Box> boxes = List.of(a, c, a);
        boxes.get(2).value = 7;
        System.out.println(a.value + " " + boxes.get(0).value + " " + boxes.get(1).value);
    }
}
