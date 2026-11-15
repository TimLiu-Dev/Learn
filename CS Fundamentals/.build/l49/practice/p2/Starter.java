import java.util.*;

public class Practice {
    record Edge(int from, int to, int weight) { }   // one-way, weight >= 0

    // Shortest distance from source to each vertex 0..n-1; null where there's no route.
    static List<Integer> shortest(int n, List<Edge> edges, int source) {
        List<List<Edge>> out = new ArrayList<>();                 // out.get(v) = the edges leaving v
        for (int v = 0; v < n; v++) out.add(new ArrayList<>());
        for (Edge e : edges) out.get(e.from()).add(e);
        throw new UnsupportedOperationException("TODO: lazy Dijkstra: a queue of {distance, vertex}; settle on poll; skip stale entries");
    }

    // CHECKS
    static List<Integer> bellmanFord(int n, List<Edge> edges, int source) {   // the oracle: no greedy step
        Integer[] dist = new Integer[n];
        dist[source] = 0;
        for (int i = 0; i < n - 1; i++)
            for (Edge e : edges)
                if (dist[e.from()] != null && (dist[e.to()] == null || dist[e.from()] + e.weight() < dist[e.to()]))
                    dist[e.to()] = dist[e.from()] + e.weight();
        return Arrays.asList(dist);
    }

    public static void main(String[] args) {
        // 0 -> 1 costs 10 directly, but 3 via 2. Vertex 4 only has an edge out; 5 has no edges.
        List<Edge> graph = List.of(new Edge(0, 1, 10), new Edge(0, 2, 1), new Edge(2, 1, 2),
            new Edge(1, 3, 1), new Edge(2, 3, 7), new Edge(4, 0, 1));
        check("the source is 0 away from itself", List.of(0), () -> shortest(1, List.of(), 0));
        check("a cheaper detour beats the direct edge", 3, () -> shortest(6, graph, 0).get(1));
        check("a vertex nothing reaches has no distance", Arrays.asList(null, null), () -> { List<Integer> d = shortest(6, graph, 0); return Arrays.asList(d.get(4), d.get(5)); });
        check("every distance on the small graph", Arrays.asList(0, 3, 1, 4, null, null), () -> shortest(6, graph, 0));
        check("edges are one-way", Arrays.asList(null, null, null, 0, null, null), () -> shortest(6, graph, 3));
        check("agrees with Bellman-Ford on 200 random graphs", "all 200 agree", () -> {
            Random rnd = new Random(49);
            for (int t = 0; t < 200; t++) {
                List<Edge> edges = new ArrayList<>();
                for (int i = 0; i < 10; i++) edges.add(new Edge(rnd.nextInt(6), rnd.nextInt(6), rnd.nextInt(10)));
                int s = rnd.nextInt(6);
                if (!bellmanFord(6, edges, s).equals(shortest(6, edges, s))) return "graph " + t + " from " + s + ": " + edges;
            }
            return "all 200 agree";
        });
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
