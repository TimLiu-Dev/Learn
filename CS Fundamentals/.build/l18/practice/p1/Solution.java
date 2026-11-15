import java.util.*;

public class Practice {
    static final class Node {
        final int value;
        final Node next;
        Node(int value, Node next) { this.value = value; this.next = next; }
    }

    // The largest value in the list, or null for an empty list.
    static Integer maxValue(Node node) {
        if (node == null) return null;               // empty list: there is no max
        Integer rest = maxValue(node.next);
        return rest == null ? node.value : Math.max(node.value, rest);
    }

    // A new list without any x; the old nodes stay unchanged.
    static Node without(Node node, int x) {
        if (node == null) return null;               // nothing left to filter
        Node rest = without(node.next, x);           // trust the recursion for the tail
        return node.value == x ? rest : new Node(node.value, rest);
    }

    // CHECKS
    static Node listOfNodes(int... xs) {
        Node n = null;
        for (int i = xs.length - 1; i >= 0; i--) n = new Node(xs[i], n);
        return n;
    }

    static List<Integer> values(Node n) {
        List<Integer> out = new ArrayList<>();
        for (; n != null; n = n.next) out.add(n.value);
        return out;
    }

    public static void main(String[] args) {
        check("an empty list has no max", null, () -> maxValue(null));
        check("max of one element is that element", 7, () -> maxValue(listOfNodes(7)));
        check("max works when every value is negative", -1, () -> maxValue(listOfNodes(-5, -1, -9)));
        check("max finds the largest anywhere", 9, () -> maxValue(listOfNodes(3, 9, 2, 9, 4)));
        check("without removes every copy, including the head", List.of(1, 3), () -> values(without(listOfNodes(2, 2, 1, 2, 3, 2), 2)));
        check("without on an empty list is empty", List.of(), () -> values(without(null, 1)));
        check("without can remove everything", List.of(), () -> values(without(listOfNodes(4, 4), 4)));
        check("without leaves the original list alone", List.of(1, 2, 3), () -> { Node o = listOfNodes(1, 2, 3); without(o, 2); return values(o); });
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
