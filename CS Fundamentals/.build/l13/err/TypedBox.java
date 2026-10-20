public class TypedBox {
    static class Box<T> {                        // T is a type parameter
        T item;
        Box(T item) { this.item = item; }
    }

    public static void main(String[] args) {
        Box<String> box = new Box<>("hello");
        System.out.println(box.item.toUpperCase());  // no cast needed
        box.item = 42;                               // caught at COMPILE time
    }
}
