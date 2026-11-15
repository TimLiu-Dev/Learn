import java.util.Objects;

public class Practice {
    static class Node<T> {
        final T value;
        Node<T> next;
        Node(T value, Node<T> next) { this.value = value; this.next = next; }
    }

    static <T> boolean hasCycle(Node<T> head) {
        throw new UnsupportedOperationException("TODO: walk the list without getting stuck in the loop");
    }

    // CHECKS
    @SafeVarargs
    static <T> Node<T>[] chain(T... values) {
        @SuppressWarnings("unchecked") Node<T>[] nodes = new Node[values.length];
        for (int i = values.length - 1; i >= 0; i--) nodes[i] = new Node<>(values[i], i + 1 < values.length ? nodes[i + 1] : null);
        return nodes;
    }

    public static void main(String[] args) {
        check("an empty list has no loop", false, () -> hasCycle(null));
        check("one node on its own has no loop", false, () -> hasCycle(new Node<>("A", null)));
        check("one node pointing at itself is a loop", true, () -> { var a = new Node<>("A", null); a.next = a; return hasCycle(a); });
        check("an ordinary list has no loop", false, () -> hasCycle(chain("A", "B", "C", "D", "E")[0]));
        check("a loop back into the middle is found", true, () -> { var n = chain("A", "B", "C", "D", "E"); n[4].next = n[2]; return hasCycle(n[0]); });
        check("equal values in different nodes are not a loop", false, () -> hasCycle(chain("x", "x", "x", "x")[0]));
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
