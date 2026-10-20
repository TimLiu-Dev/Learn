import java.util.*;

public class Dfs {
    static final Map<String, List<String>> GRAPH = Map.of(
        "A", List.of("B", "C"), "B", List.of("A", "D"), "C", List.of("A", "D", "E"),
        "D", List.of("B", "C", "F"), "E", List.of("C", "F", "H"), "F", List.of("D", "E", "G"),
        "G", List.of("F"), "H", List.of("E"));

    // Recursive: the call stack (L4) remembers where to come back to.
    static void dfs(String v, Set<String> visited, List<String> order) {
        visited.add(v);
        order.add(v);
        for (String n : GRAPH.get(v)) {
            if (!visited.contains(n)) dfs(n, visited, order);
        }
    }

    // Iterative: an explicit stack replaces the call stack.
    static List<String> dfsIterative(String start, boolean reversePush) {
        Set<String> visited = new HashSet<>();
        List<String> order = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>(List.of(start));
        while (!stack.isEmpty()) {
            String v = stack.removeLast();
            if (!visited.add(v)) continue;     // pushed twice; already done
            order.add(v);
            List<String> ns = GRAPH.get(v);
            if (reversePush) ns = ns.reversed();
            for (String n : ns) {
                if (!visited.contains(n)) stack.addLast(n);
            }
        }
        return order;
    }

    public static void main(String[] args) {
        List<String> order = new ArrayList<>();
        dfs("A", new HashSet<>(), order);
        System.out.println("recursive          " + order);
        System.out.println("stack              " + dfsIterative("A", false));
        System.out.println("stack, reversed    " + dfsIterative("A", true));
    }
}
