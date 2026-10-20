import java.util.*;

public class Ex1 {
    static class SinglyList<T> {
        private static class Node<T> {
            final T value;
            Node<T> next;
            Node(T value, Node<T> next) { this.value = value; this.next = next; }
        }

        private Node<T> head;
        private int size;

        int size() { return size; }

        void addFirst(T value) {                    // O(1)
            head = new Node<>(value, head);
            size++;
        }

        T get(int index) {                          // O(n): walk index steps
            Objects.checkIndex(index, size);        // throws IndexOutOfBoundsException
            Node<T> cur = head;
            for (int i = 0; i < index; i++) cur = cur.next;
            return cur.value;
        }

        T removeFirst() {                           // O(1)
            if (head == null) throw new NoSuchElementException("list is empty");
            Node<T> h = head;
            head = h.next;
            size--;
            return h.value;
        }

        boolean contains(T value) {                 // O(n)
            for (Node<T> cur = head; cur != null; cur = cur.next) {
                if (Objects.equals(cur.value, value)) return true;
            }
            return false;
        }

        boolean removeValue(T value) {              // O(n): find the node BEFORE it
            if (head == null) return false;
            if (Objects.equals(head.value, value)) { head = head.next; size--; return true; }
            Node<T> prev = head;
            for (Node<T> cur = head.next; cur != null; prev = cur, cur = cur.next) {
                if (Objects.equals(cur.value, value)) {
                    prev.next = cur.next;           // skip over cur
                    size--;
                    return true;
                }
            }
            return false;
        }

        @Override public String toString() {
            StringJoiner sj = new StringJoiner(", ", "[", "]");
            for (Node<T> cur = head; cur != null; cur = cur.next) sj.add(String.valueOf(cur.value));
            return sj.toString();
        }
    }

    public static void main(String[] args) {
        SinglyList<String> list = new SinglyList<>();
        list.addFirst("C"); list.addFirst("B"); list.addFirst("A");
        System.out.println(list + " size=" + list.size() + " list[1]=" + list.get(1));
        System.out.println("contains B? " + list.contains("B") + "  contains Z? " + list.contains("Z"));
        System.out.println("remove B: " + list.removeValue("B") + " -> " + list);
        System.out.println("remove A: " + list.removeValue("A") + " -> " + list);
        System.out.println("remove Z: " + list.removeValue("Z") + " -> " + list);
        System.out.println("removeFirst: " + list.removeFirst() + " -> " + list + " size=" + list.size());
        try { list.removeFirst(); } catch (NoSuchElementException e) { System.out.println("empty: " + e.getMessage()); }
        try { list.get(0); } catch (IndexOutOfBoundsException e) { System.out.println("bad index: " + e.getMessage()); }
    }
}
