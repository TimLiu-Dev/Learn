import java.util.*;

public class Bugs {
    static class Node { final String value; Node next; Node(String v, Node n) { value = v; next = n; } }

    // BUG 1: overwrite node.next first, and the only link to the rest is gone
    static void insertAfterBuggy(Node node, String value) {
        Node fresh = new Node(value, null);
        node.next = fresh;
        fresh.next = node.next;         // node.next IS fresh now: it points at itself
    }

    static void insertAfter(Node node, String value) {
        node.next = new Node(value, node.next);  // read the old link before replacing it
    }

    // BUG 2: a list with a tail pointer that forgets to clear it
    static class TailList {
        Node head, tail;
        int size;
        void addLast(String value) {
            Node n = new Node(value, null);
            if (tail == null) { head = n; tail = n; } else { tail.next = n; tail = n; }
            size++;
        }
        String removeFirst() {
            if (head == null) throw new NoSuchElementException();
            Node h = head;
            head = h.next;
            size--;
            // missing: if (head == null) tail = null;
            return h.value;
        }
    }

    static String show(Node head) {
        List<String> parts = new ArrayList<>();
        Node cur = head;
        while (cur != null && parts.size() < 6) { parts.add(cur.value); cur = cur.next; }
        return String.join(" -> ", parts) + (cur != null ? " -> ..." : "");
    }

    public static void main(String[] args) {
        Node a = new Node("A", new Node("B", new Node("C", null)));
        insertAfter(a, "X");
        System.out.println("fixed: " + show(a));
        Node b = new Node("A", new Node("B", new Node("C", null)));
        insertAfterBuggy(b, "X");
        System.out.println("buggy: " + show(b));

        TailList list = new TailList();
        list.addLast("A");
        list.removeFirst();
        list.addLast("B");
        System.out.println("size=" + list.size + " contents=[" + show(list.head) + "]");
    }
}
