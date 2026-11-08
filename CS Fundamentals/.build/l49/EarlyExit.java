import java.nio.file.*;
import java.util.*;

public class EarlyExit {
    record Arc(int to, double weight) {}
    record Entry(double dist, int v) {}

    // algs4 format: V, then E, then one "from to weight" line per directed edge.
    static List<List<Arc>> load(String path) throws Exception {
        List<String> lines = Files.readAllLines(Path.of(path)).stream().filter(l -> !l.isBlank()).toList();
        int n = Integer.parseInt(lines.get(0).trim());
        List<List<Arc>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (String line : lines.subList(2, lines.size())) {
            String[] f = line.trim().split("\\s+");
            adj.get(Integer.parseInt(f[0])).add(new Arc(Integer.parseInt(f[1]), Double.parseDouble(f[2])));
        }
        return adj;
    }

    // Returns {distance to target, how many vertices were settled before stopping}.
    static double[] search(List<List<Arc>> adj, int s, Integer target) {
        double[] dist = new double[adj.size()];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        boolean[] settled = new boolean[adj.size()];
        int count = 0;
        PriorityQueue<Entry> pq = new PriorityQueue<>(Comparator.comparingDouble(Entry::dist));
        dist[s] = 0.0;
        pq.add(new Entry(0.0, s));
        while (!pq.isEmpty()) {
            Entry top = pq.poll();
            int v = top.v();
            if (settled[v]) continue;
            settled[v] = true;
            count++;
            if (target != null && v == target) return new double[] {top.dist(), count};   // its distance is final: stop here
            for (Arc a : adj.get(v)) {
                if (!settled[a.to()] && top.dist() + a.weight() < dist[a.to()]) {
                    dist[a.to()] = top.dist() + a.weight();
                    pq.add(new Entry(dist[a.to()], a.to()));
                }
            }
        }
        return new double[] {target == null ? 0.0 : dist[target], count};
    }

    public static void main(String[] args) throws Exception {
        List<List<Arc>> adj = load("mediumEWD.txt");
        System.out.println("full run from 0 settles " + (int) search(adj, 0, null)[1] + " of " + adj.size() + " vertices");
        for (int t : new int[] {44, 4, 1, 2}) {
            double[] r = search(adj, 0, t);
            System.out.println(String.format("0 -> %-3d %.2f  after settling %3d vertices", t, r[0], (int) r[1]));
        }
    }
}
