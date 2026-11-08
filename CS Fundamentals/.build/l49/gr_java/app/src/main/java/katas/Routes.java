package katas;

import java.util.*;

public final class Routes {
    private Routes() {}

    public record Edge(int from, int to, double weight) {}
    public record Graph(int vertexCount, List<Edge> edges) {}
    public record Route(double distance, List<Integer> vertices) {}
    private record Entry(double dist, int v) {}

    // algs4 format: V, then E, then one "from to weight" line per edge.
    public static Graph parseGraph(String text) {
        List<String> lines = text.lines().filter(l -> !l.isBlank()).toList();
        List<Edge> edges = new ArrayList<>();
        for (String line : lines.subList(2, lines.size())) {
            String[] f = line.trim().split("\\s+");
            edges.add(new Edge(Integer.parseInt(f[0]), Integer.parseInt(f[1]), Double.parseDouble(f[2])));
        }
        return new Graph(Integer.parseInt(lines.get(0).trim()), List.copyOf(edges));
    }

    // Dijkstra, lazy version. Edges are one-way. Empty if `to` can't be reached.
    public static Optional<Route> shortestPath(Graph g, int from, int to) {
        if (g.edges().stream().anyMatch(e -> e.weight() < 0))
            throw new IllegalArgumentException("Dijkstra needs non-negative weights");
        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i < g.vertexCount(); i++) adj.add(new ArrayList<>());
        for (Edge e : g.edges()) adj.get(e.from()).add(e);
        double[] dist = new double[g.vertexCount()];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Edge[] edgeTo = new Edge[g.vertexCount()];
        boolean[] settled = new boolean[g.vertexCount()];
        PriorityQueue<Entry> pq = new PriorityQueue<>(Comparator.comparingDouble(Entry::dist));
        dist[from] = 0.0;
        pq.add(new Entry(0.0, from));
        while (!pq.isEmpty()) {
            Entry top = pq.poll();
            int v = top.v();
            if (settled[v]) continue;
            settled[v] = true;
            if (v == to) break;                                   // final once settled
            for (Edge e : adj.get(v)) {
                if (!settled[e.to()] && top.dist() + e.weight() < dist[e.to()]) {
                    dist[e.to()] = top.dist() + e.weight();
                    edgeTo[e.to()] = e;
                    pq.add(new Entry(dist[e.to()], e.to()));
                }
            }
        }
        if (dist[to] == Double.POSITIVE_INFINITY) return Optional.empty();
        List<Integer> path = new ArrayList<>();
        for (int v = to; ; v = edgeTo[v].from()) {
            path.add(v);
            if (edgeTo[v] == null) break;
        }
        Collections.reverse(path);
        return Optional.of(new Route(dist[to], List.copyOf(path)));
    }

    // The slow, simple oracle for tests: relax every edge V-1 times.
    public static double[] bellmanFord(Graph g, int from) {
        double[] dist = new double[g.vertexCount()];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[from] = 0.0;
        for (int pass = 0; pass < g.vertexCount() - 1; pass++)
            for (Edge e : g.edges())
                if (dist[e.from()] + e.weight() < dist[e.to()]) dist[e.to()] = dist[e.from()] + e.weight();
        return dist;
    }

    // Kruskal. Edges count as two-way here. Empty if the graph isn't connected.
    public static Optional<List<Edge>> cheapestNetwork(Graph g) {
        List<Edge> sorted = new ArrayList<>(g.edges());
        sorted.sort(Comparator.comparingDouble(Edge::weight));
        DisjointSets sets = new DisjointSets(g.vertexCount());
        List<Edge> tree = new ArrayList<>();
        for (Edge e : sorted) {
            if (tree.size() == g.vertexCount() - 1) break;
            if (sets.union(e.from(), e.to())) tree.add(e);
        }
        return tree.size() == g.vertexCount() - 1 ? Optional.of(List.copyOf(tree)) : Optional.empty();
    }
}
