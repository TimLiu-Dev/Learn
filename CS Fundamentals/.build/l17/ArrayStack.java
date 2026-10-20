import java.util.*;

public class ArrayStack {
    static class Stack<T> {
        private final ArrayList<T> items = new ArrayList<>();  // the top is the END of the array

        void push(T item) { items.add(item); }      // amortised O(1), like L16's add
        T pop() {                                   // O(1): nothing shifts
            if (items.isEmpty()) throw new NoSuchElementException("stack is empty");
            return items.remove(items.size() - 1);
        }
        T peek() { return items.isEmpty() ? null : items.get(items.size() - 1); }  // O(1)
        boolean isEmpty() { return items.isEmpty(); }
        int size() { return items.size(); }
    }

    public static void main(String[] args) {
        Stack<String> undo = new Stack<>();
        undo.push("type 'Hi'");
        undo.push("bold");
        undo.push("delete word");
        System.out.println("peek: " + undo.peek() + "  size: " + undo.size());
        while (!undo.isEmpty()) System.out.println("undo " + undo.pop());  // last in, first out
        try { undo.pop(); } catch (NoSuchElementException e) { System.out.println("pop: " + e.getMessage()); }
    }
}
