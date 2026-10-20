import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Stack<T> {
    private final List<T> items = new ArrayList<>();

    public void push(T item) { items.add(item); }

    public T pop() {
        if (items.isEmpty()) throw new IllegalStateException("pop() on an empty stack");
        return items.remove(items.size() - 1);
    }

    public T peek() { return items.isEmpty() ? null : items.get(items.size() - 1); }  // null when empty

    public boolean isEmpty() { return items.isEmpty(); }

    // producer: Iterable of T or any subtype
    public void pushAll(Iterable<? extends T> src) { for (T item : src) push(item); }

    // consumer: collection of T or any supertype
    public void popAllInto(Collection<? super T> dst) { while (!isEmpty()) dst.add(pop()); }

    @Override public String toString() { return items.toString(); }

    static <T extends Comparable<T>> T maxOf(List<T> items) {
        if (items.isEmpty()) return null;
        T best = items.get(0);
        for (T item : items) if (item.compareTo(best) > 0) best = item;
        return best;
    }

    public static void main(String[] args) {
        Stack<Number> numbers = new Stack<>();
        List<Integer> ints = List.of(1, 2, 3);
        numbers.pushAll(ints);                        // List<Integer> into Stack<Number>
        System.out.println("pushed ints: " + numbers);
        System.out.println("peek: " + numbers.peek());
        numbers.push(2.5);                            // a Double is a Number too
        System.out.println("pop: " + numbers.pop());

        List<Object> log = new ArrayList<>(List.of("log:"));
        numbers.popAllInto(log);                      // Stack<Number> into List<Object>
        System.out.println("drained: " + log + ", empty now: " + numbers.isEmpty());
        System.out.println("peek on empty: " + numbers.peek());
        try {
            numbers.pop();
        } catch (IllegalStateException e) {
            System.out.println("pop on empty: " + e.getMessage());
        }

        System.out.println("max: " + maxOf(List.of(4, 9, 2)) + " " + maxOf(List.of("pear", "fig"))
                + " " + maxOf(List.<Integer>of()));
    }
}
