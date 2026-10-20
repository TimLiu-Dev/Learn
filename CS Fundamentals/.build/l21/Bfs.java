import java.util.*;

public class Bfs {
    static final Map<String, List<String>> GRAPH = Map.of(
        "A", List.of("B", "C"), "B", List.of("A", "D"), "C", List.of("A", "D", "E"),
        "D", List.of("B", "C", "F"), "E", List.of("C", "F", "H"), "F", List.of("D", "E", "G"),
        "G", List.of("F"), "H", List.of("E"), "Z", List.of());

    // Returns each reachable vertex's parent on a shortest path from start.
    static Map<String, String> bfs(String start) {
        Set<String> visited = new HashSet<>(Set.of(start));
        Map<String, String> parent = new HashMap<>();
        parent.put(start, null);
        Deque<String> queue = new ArrayDeque<>(List.of(start));
        while (!queue.isEmpty()) {
            String v = queue.removeFirst();
            for (String n : GRAPH.get(v)) {
                if (visited.add(n)) {          // add returns false if n was already there
                    parent.put(n, v);
                    queue.addLast(n);
                }
            }
            System.out.println("visit " + v + "  queue=" + queue);
        }
        return parent;
    }

    static List<String> pathTo(Map<String, String> parent, String target) {
        List<String> path = new ArrayList<>();
        if (!parent.containsKey(target)) return path;
        for (String v = target; v != null; v = parent.get(v)) path.add(v);
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {
        Map<String, String> parent = bfs("A");
        for (String target : List.of("G", "H", "Z")) {
            List<String> path = pathTo(parent, target);
            System.out.println("A -> " + target + ": " + path + "  "
                + (path.isEmpty() ? "(unreachable)" : "(" + (path.size() - 1) + " edges)"));
        }
    }
}
