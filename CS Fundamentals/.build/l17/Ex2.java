import java.util.*;

// A recently-used list: the core of an LRU cache (Phase 12 builds the cache itself)
public class Ex2 {
    static class RecentList<T> {
        static class Node<T> { final T value; Node<T> prev, next; Node(T v) { value = v; } }

        private final Node<T> head = new Node<>(null);
        private final Node<T> tail = new Node<>(null);
        private int size;

        RecentList() { head.next = tail; tail.prev = head; }

        int size() { return size; }

        private void unlink(Node<T> n) {
            n.prev.next = n.next;
            n.next.prev = n.prev;
        }

        private void linkFirst(Node<T> n) {
            n.prev = head; n.next = head.next;
            head.next.prev = n; head.next = n;
        }

        Node<T> addFirst(T value) {
            Node<T> n = new Node<>(value);
            linkFirst(n);
            size++;
            return n;
        }

        void moveToFront(Node<T> n) {               // O(1): unlink, relink at the front
            unlink(n);
            linkFirst(n);
        }

        T removeLast() {                            // O(1): the least recently used
            Node<T> last = tail.prev;
            if (last == head) throw new NoSuchElementException("list is empty");
            unlink(last);
            size--;
            return last.value;
        }

        @Override public String toString() {
            List<String> parts = new ArrayList<>();
            for (Node<T> cur = head.next; cur != tail; cur = cur.next) parts.add(String.valueOf(cur.value));
            return parts.toString();
        }
    }

    public static void main(String[] args) {
        RecentList<String> recent = new RecentList<>();
        RecentList.Node<String> a = recent.addFirst("a.txt");
        recent.addFirst("b.txt");
        recent.addFirst("c.txt");
        System.out.println(recent);                 // newest first
        recent.moveToFront(a);                      // a.txt was opened again
        System.out.println(recent);
        String evicted = recent.removeLast();
        System.out.println("evict " + evicted + " -> " + recent + " size=" + recent.size());
    }
}
