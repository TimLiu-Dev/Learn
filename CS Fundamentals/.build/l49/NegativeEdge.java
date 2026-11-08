import java.util.*;

public class NegativeEdge {
    record Edge(String from, String to, int weight) {}
    record Entry(int dist, String v) {}

    // One-way edges. C -> B is negative: say, a road with a toll refund.
    static final List<Edge> EDGES = List.of(new Edge("A", "B", 2), new Edge("A", "C", 5), new Edge("C", "B", -4));
    static final List<String> VERTICES = List.of("A", "B", "C");

    // The same lazy Dijkstra as Dijkstra.java, without the printing.
    static Map<String, Integer> dijkstra(String start) {
        Map<String, Integer> dist = new TreeMap<>(Map.of(start, 0));
        Set<String> settled = new HashSet<>();
        PriorityQueue<Entry> pq = new PriorityQueue<>(Comparator.comparingInt(Entry::dist).thenComparing(Entry::v));
        pq.add(new Entry(0, start));
        while (!pq.isEmpty()) {
            Entry top = pq.poll();
            if (!settled.add(top.v())) continue;
            for (Edge e : EDGES) {
                if (!e.from().equals(top.v()) || settled.contains(e.to())) continue;
                int nd = top.dist() + e.weight();
                if (nd < dist.getOrDefault(e.to(), Integer.MAX_VALUE)) {
                    dist.put(e.to(), nd);
                    pq.add(new Entry(nd, e.to()));
                }
            }
        }
        return dist;
    }

    // Bellman-Ford: relax every edge, V-1 times. Slow, O(V·E), but negative edges are fine.
    static Map<String, Integer> bellmanFord(String start) {
        Map<String, Integer> dist = new TreeMap<>(Map.of(start, 0));
        for (int pass = 0; pass < VERTICES.size() - 1; pass++) {   // a shortest path uses at most V-1 edges
            for (Edge e : EDGES) {
                Integer d = dist.get(e.from());
                if (d == null) continue;                            // not reached yet
                if (d + e.weight() < dist.getOrDefault(e.to(), Integer.MAX_VALUE)) dist.put(e.to(), d + e.weight());
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        System.out.println("dijkstra:     " + dijkstra("A"));
        System.out.println("bellman-ford: " + bellmanFord("A"));
    }
}
