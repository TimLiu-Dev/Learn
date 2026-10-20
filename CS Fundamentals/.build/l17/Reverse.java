public class Reverse {
    static class Node { final int value; Node next; Node(int v, Node n) { value = v; next = n; } }

    static Node reverse(Node head) {
        Node prev = null;
        Node curr = head;
        while (curr != null) {
            Node next = curr.next;      // 1. remember the rest of the list
            curr.next = prev;           // 2. flip this node's arrow backwards
            prev = curr;                // 3. step prev forward
            curr = next;                // 4. step curr forward
        }
        return prev;                    // the old tail is the new head
    }

    public static void main(String[] args) {
        Node list = new Node(1, new Node(2, new Node(3, null)));
        Node reversed = reverse(list);
        for (Node n = reversed; n != null; n = n.next) System.out.print(n.value + " ");
        System.out.println();
        System.out.println(list.next == null);  // the old head is now the tail
    }
}
