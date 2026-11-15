import java.util.*;

public class Practice {
    static class DoublyList<T> implements Iterable<T> {
        static class Node<T> {
            final T value;                          // null only in the sentinels
            Node<T> prev, next;
            Node(T value) { this.value = value; }
        }

        private Node<T> head = new Node<>(null);    // sentinel before the first element
        private Node<T> tail = new Node<>(null);    // sentinel after the last element
        private int size = 0;

        DoublyList() { head.next = tail; tail.prev = head; }

        private Node<T> linkBefore(Node<T> succ, T value) {
            Node<T> pred = succ.prev;
            Node<T> n = new Node<>(value);
            n.prev = pred; n.next = succ;
            pred.next = n; succ.prev = n;
            size++;
            return n;
        }

        Node<T> addFirst(T value) { return linkBefore(head.next, value); }
        Node<T> addLast(T value) { return linkBefore(tail, value); }

        T remove(Node<T> node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
            return node.value;
        }

        void reverse() {
            throw new UnsupportedOperationException("TODO: flip the list in O(n): no new nodes, no moved values");
        }

        int size() { return size; }

        @Override public Iterator<T> iterator() {
            return new Iterator<T>() {
                private Node<T> cur = head.next;
                @Override public boolean hasNext() { return cur != tail; }
                @Override public T next() {
                    if (!hasNext()) throw new NoSuchElementException();
                    T v = cur.value;
                    cur = cur.next;
                    return v;
                }
            };
        }

        List<T> toList() {
            List<T> out = new ArrayList<>();
            for (T v : this) out.add(v);
            return out;
        }

        List<T> backwards() {
            List<T> out = new ArrayList<>();
            for (Node<T> cur = tail.prev; cur != head; cur = cur.prev) out.add(cur.value);
            return out;
        }
    }

    // CHECKS
    static DoublyList<String> listOfValues(String... values) {
        DoublyList<String> list = new DoublyList<>();
        for (String v : values) list.addLast(v);
        return list;
    }

    public static void main(String[] args) {
        check("reversing an empty list leaves it empty", "[] [] 0", () -> { var l = listOfValues(); l.reverse(); return l.toList() + " " + l.backwards() + " " + l.size(); });
        check("a one-element list stays the same", "[A] [A]", () -> { var l = listOfValues("A"); l.reverse(); return l.toList() + " " + l.backwards(); });
        check("reverse flips the order in both directions", "[D, C, B, A] [A, B, C, D]", () -> { var l = listOfValues("A", "B", "C", "D"); l.reverse(); return l.toList() + " " + l.backwards(); });
        check("adds land at the right ends after a reverse", "[Y, C, B, A, Z] [Z, A, B, C, Y]", () -> { var l = listOfValues("A", "B", "C"); l.reverse(); l.addLast("Z"); l.addFirst("Y"); return l.toList() + " " + l.backwards(); });
        check("a node you hold still works after a reverse", "B [C, A] [A, C]", () -> { var l = new DoublyList<String>(); l.addLast("A"); var b = l.addLast("B"); l.addLast("C"); l.reverse(); return l.remove(b) + " " + l.toList() + " " + l.backwards(); });
        check("reversing twice gives the original back", "[A, B, C] [C, B, A]", () -> { var l = listOfValues("A", "B", "C"); l.reverse(); l.reverse(); return l.toList() + " " + l.backwards(); });
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
