import java.util.*;

public class NoVisited {
    static final Map<String, List<String>> GRAPH = Map.of(
        "A", List.of("B", "C"), "B", List.of("A", "D"), "C", List.of("A", "D", "E"),
        "D", List.of("B", "C", "F"), "E", List.of("C", "F", "H"), "F", List.of("D", "E", "G"),
        "G", List.of("F"), "H", List.of("E"));

    // BFS with the visited set deleted. Capped at 20 steps so it can't run forever.
    public static void main(String[] args) {
        Deque<String> queue = new ArrayDeque<>(List.of("A"));
        Map<String, Integer> seen = new TreeMap<>();
        for (int step = 0; step < 20; step++) {
            String v = queue.removeFirst();
            seen.merge(v, 1, Integer::sum);
            queue.addAll(GRAPH.get(v));
        }
        System.out.println("after 20 steps: queue size " + queue.size() + ", visit counts " + seen);
    }
}
