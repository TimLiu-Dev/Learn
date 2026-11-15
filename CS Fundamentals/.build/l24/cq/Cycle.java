public class Cycle {
    static class Node {
        final String name;
        Node next;
        Node(String name) { this.name = name; }
    }

    public static void main(String[] args) {
        Node head = new Node("A");
        head.next = new Node("B");
        head.next.next = head;       // B points back at A: a cycle
        Node tail = head.next;       // a second variable, referring to B
        head = null;                 // no variable refers to A now
        System.out.println(tail.next.name);
    }
}
