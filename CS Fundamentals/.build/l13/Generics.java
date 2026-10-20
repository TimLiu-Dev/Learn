import java.util.ArrayList;
import java.util.List;

public class Generics {
    static class Box<T> {
        T item;
        Box(T item) { this.item = item; }
    }

    static class Labeled<A, B> {                          // two type parameters
        final A label;
        final B value;
        Labeled(A label, B value) { this.label = label; this.value = value; }
        <C> Labeled<A, C> withValue(C newValue) { return new Labeled<>(label, newValue); }
        @Override public String toString() { return label + "=" + value; }
    }

    static <T> T firstOrNull(List<T> items) {             // <T> goes BEFORE the return type
        return items.isEmpty() ? null : items.get(0);
    }

    static <T> List<T> repeatItem(T item, int times) {
        List<T> out = new ArrayList<>();
        for (int i = 0; i < times; i++) out.add(item);
        return out;
    }

    public static void main(String[] args) {
        Box<String> b = new Box<>("hi");                  // diamond: <> infers String
        var n = new Box<>(7);                             // var + diamond: Box<Integer>
        Box<Number> explicit = new Box<Number>(7);        // fully spelled out
        System.out.println(b.item.length() + " " + (n.item + 1) + " " + explicit.item);

        Labeled<String, Integer> price = new Labeled<>("price", 250);
        Labeled<String, String> shown = price.withValue("$2.50");
        System.out.println(price + " " + shown);

        System.out.println(firstOrNull(List.of("a", "b")));
        System.out.println(Generics.<Integer>firstOrNull(List.of())); // explicit type argument
        System.out.println(repeatItem('x', 3));
    }
}
