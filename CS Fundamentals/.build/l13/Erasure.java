import java.util.ArrayList;

public class Erasure {
    static class Box<T> {
        final T item;
        Box(T item) { this.item = item; }
        T get() { return item; }
    }

    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        ArrayList<Integer> ints = new ArrayList<>();
        System.out.println(strings.getClass().getName());
        System.out.println(strings.getClass() == ints.getClass());   // one class at runtime
        System.out.println(new Box<>("a").getClass() == new Box<>(1).getClass());

        Box<String> box = new Box<>("hi");
        String s = box.get();              // compiler inserts a hidden cast here
        System.out.println(s);
    }
}
