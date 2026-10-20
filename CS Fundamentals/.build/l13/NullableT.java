import java.util.Objects;

public class NullableT {
    static class Box<T> {                    // any T can hold null in Java
        final T item;
        Box(T item) { this.item = item; }
    }

    static class Strict<T> {                 // no "T : Any": check at runtime instead
        final T item;
        Strict(T item) { this.item = Objects.requireNonNull(item, "item"); }
    }

    static <T> String describe(T x) { return "got " + x; }

    public static void main(String[] args) {
        Box<String> maybe = new Box<>(null);  // compiles: the type says nothing about null
        System.out.println(maybe.item == null ? null : maybe.item.length());
        System.out.println(describe(null));
        Strict<String> sure = new Strict<>("hi");
        System.out.println(sure.item.length());
        new Strict<String>(null);             // compiles, fails when it runs
    }
}
