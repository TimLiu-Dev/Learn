import java.util.*;

public class Practice {
    // The heap: each object's name -> the names it refers to.
    static Set<String> mark(List<String> roots, Map<String, List<String>> heap) {
        Set<String> marked = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>(roots);   // the mark stack: reached, not yet scanned
        while (!stack.isEmpty()) {
            String obj = stack.pop();
            // add() returns false for an object already marked, so a cycle stops here.
            if (marked.add(obj)) heap.get(obj).forEach(stack::push);
        }
        return marked;
    }

    static Set<String> sweep(List<String> roots, Map<String, List<String>> heap) {
        Set<String> garbage = new HashSet<>(heap.keySet());
        garbage.removeAll(mark(roots, heap));
        return garbage;
    }

    // CHECKS
    // The mark-and-sweep diagram from Part 1, without the root nodes.
    static final Map<String, List<String>> DIAGRAM = Map.of(
        "Order #2", List.of("Customer", "List<Item>"),
        "Customer", List.of(),
        "List<Item>", List.of(),
        "line 7", List.of(),
        "Order #1", List.of("Customer"),
        "Node A", List.of("Node B"),
        "Node B", List.of("Node A"),
        "Config", List.of("Settings"),
        "Settings", List.of());

    static Map<String, List<String>> chain(int n) {
        Map<String, List<String>> heap = new HashMap<>();
        for (int i = 0; i < n; i++) heap.put("o" + i, i + 1 < n ? List.of("o" + (i + 1)) : List.of());
        return heap;
    }

    public static void main(String[] args) {
        check("in the diagram, the cycle and the dead order are swept", Set.of("line 7", "Order #1", "Node A", "Node B"), () -> sweep(List.of("Order #2", "Config"), DIAGRAM));
        check("mark follows references all the way down", Set.of("a", "b", "c", "d"), () -> mark(List.of("a"), Map.of("a", List.of("b"), "b", List.of("c"), "c", List.of("d"), "d", List.of())));
        check("a cycle that a root reaches stays alive", "[x, y] [z]", () -> { var heap = Map.of("x", List.of("y"), "y", List.of("x"), "z", List.of("x")); return new TreeSet<>(mark(List.of("x"), heap)) + " " + sweep(List.of("x"), heap); });
        check("with no roots, everything is garbage", DIAGRAM.keySet(), () -> sweep(List.of(), DIAGRAM));
        check("a chain of 100,000 objects is marked without a stack overflow", 100_000, () -> mark(List.of("o0"), chain(100_000)).size());
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
