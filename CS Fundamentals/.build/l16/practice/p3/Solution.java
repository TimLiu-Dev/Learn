import java.util.*;

public class Practice {
    // A stack on a resizing array: doubles when full, halves when a quarter full.
    static class ResizingStack<T> {
        private Object[] data = new Object[2];
        int size = 0;
        long copies = 0;                                  // elements copied by resizes so far

        int capacity() { return data.length; }

        private void resize(int newCapacity) {
            copies += size;
            data = Arrays.copyOf(data, newCapacity);      // a new array, holding the first `size` elements
        }

        void push(T x) {
            if (size == data.length) resize(data.length * 2);
            data[size++] = x;
        }

        @SuppressWarnings("unchecked")                    // safe: only push puts things in data, and they're all T
        T pop() {
            if (size == 0) throw new NoSuchElementException("pop on an empty stack");
            T top = (T) data[--size];
            data[size] = null;                            // don't keep a removed element alive (Item 7)
            if (size > 0 && size == data.length / 4) resize(data.length / 2);   // now half full: room both ways
            return top;
        }
    }

    // CHECKS
    static ResizingStack<Integer> stackOf(int n) {
        ResizingStack<Integer> s = new ResizingStack<>();
        for (int i = 1; i <= n; i++) s.push(i);
        return s;
    }

    public static void main(String[] args) {
        check("pop returns elements in reverse order", List.of(3, 2, 1, 0), () -> {
            ResizingStack<Integer> s = stackOf(3);
            return List.of(s.pop(), s.pop(), s.pop(), s.size);
        });
        check("pop on an empty stack throws NoSuchElementException", "NoSuchElementException", () -> {
            try { new ResizingStack<String>().pop(); return "nothing thrown"; }
            catch (NoSuchElementException e) { return "NoSuchElementException"; }
        });
        check("halves when a quarter full, not before", List.of(8, 8, 4), () -> {
            ResizingStack<Integer> s = stackOf(8);
            int full = s.capacity();
            for (int i = 0; i < 5; i++) s.pop();
            int atThree = s.capacity();
            s.pop();
            return List.of(full, atThree, s.capacity());
        });
        check("elements survive a shrink", List.of(8, 4, 3, 2, 1), () -> {
            ResizingStack<Integer> s = stackOf(16);
            for (int i = 0; i < 12; i++) s.pop();
            return List.of(s.capacity(), s.pop(), s.pop(), s.pop(), s.pop());
        });
        check("push and pop at a boundary don't thrash", true, () -> {
            ResizingStack<Integer> s = stackOf(8);
            long before = s.copies;
            for (int i = 0; i < 1_000; i++) { s.push(0); s.pop(); }
            return s.copies - before < 100;
        });
        check("a long run copies fewer than 3 elements per operation", true, () -> {
            ResizingStack<Integer> s = stackOf(10_000);
            for (int i = 0; i < 10_000; i++) s.pop();
            return s.copies < 3 * 20_000;
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
