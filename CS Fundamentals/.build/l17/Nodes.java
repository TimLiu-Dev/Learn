public class Nodes {
    static class Node<T> {                       // static: no hidden link to a Nodes object
        final T value;
        Node<T> next;
        Node(T value, Node<T> next) { this.value = value; this.next = next; }
    }

    public static void main(String[] args) {
        Node<String> head = null;
        head = new Node<>("C", head);            // addFirst: the new node points at the old head
        head = new Node<>("B", head);
        head = new Node<>("A", head);
        printAll(head);
        head.next = head.next.next;              // unlink B: A now points straight at C
        printAll(head);
    }

    static void printAll(Node<String> head) {
        StringBuilder sb = new StringBuilder();
        for (Node<String> cur = head; cur != null; cur = cur.next) {
            sb.append(cur.value).append(" -> ");
        }
        System.out.println(sb.append("null"));
    }
}
