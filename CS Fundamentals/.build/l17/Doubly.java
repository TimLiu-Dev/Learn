import java.util.*;

public class Doubly {
    static class DoublyList<T> implements Iterable<T> {
        static class Node<T> {
            final T value;                          // null only in the sentinels
            Node<T> prev, next;
            Node(T value) { this.value = value; }
        }

        private final Node<T> head = new Node<>(null);  // sentinel before the first element
        private final Node<T> tail = new Node<>(null);  // sentinel after the last element
        private int size;

        DoublyList() { head.next = tail; tail.prev = head; }  // empty: the sentinels hold hands

        int size() { return size; }

        private Node<T> linkBefore(Node<T> succ, T value) {
            Node<T> pred = succ.prev;
            Node<T> n = new Node<>(value);
            n.prev = pred; n.next = succ;           // 1. wire the new node in
            pred.next = n; succ.prev = n;           // 2. then point the neighbours at it
            size++;
            return n;
        }

        Node<T> addFirst(T value) { return linkBefore(head.next, value); }  // O(1)
        Node<T> addLast(T value) { return linkBefore(tail, value); }        // O(1)

        T remove(Node<T> node) {                    // O(1) when you already hold the node
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
            return node.value;
        }

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

        List<T> backwards() {
            List<T> out = new ArrayList<>();
            for (Node<T> cur = tail.prev; cur != head; cur = cur.prev) out.add(cur.value);
            return out;
        }
    }

    public static void main(String[] args) {
        DoublyList<String> list = new DoublyList<>();
        list.addLast("B");
        DoublyList.Node<String> c = list.addLast("C");
        list.addFirst("A");
        list.addLast("D");
        for (String s : list) System.out.print(s + " ");    // works because of Iterable
        System.out.println("| backwards " + list.backwards());
        List<String> now = new ArrayList<>();
        String removed = list.remove(c);
        list.forEach(now::add);                     // Iterable gives you forEach, nothing more
        System.out.println("removed " + removed + ", now " + now + " size=" + list.size());
    }
}
