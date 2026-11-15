public class Prepend {
    static class Node {
        final int value;
        Node next;
        Node(int value, Node next) { this.value = value; this.next = next; }
    }

    static void addFirst(Node head, int value) {
        head = new Node(value, head);
    }

    public static void main(String[] args) {
        Node head = new Node(1, new Node(2, null));
        addFirst(head, 0);
        StringBuilder sb = new StringBuilder();
        for (Node n = head; n != null; n = n.next) sb.append(n.value).append(' ');
        System.out.println(sb.toString().trim());
    }
}
