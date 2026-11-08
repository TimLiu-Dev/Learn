import java.util.*;

public class DijkstraTiming {
    // A random sparse digraph with V vertices and 4V edges, weights 1..100.
    // Compressed adjacency: v's edges are to[first[v]] .. to[first[v + 1] - 1].
    static final class Graph {
        final int v;
        final int[] first, to, w;
        Graph(int v, long seed) {
            this.v = v;
            int e = 4 * v;
            Random rnd = new Random(seed);
            int[] from = new int[e], dst = new int[e], wt = new int[e];
            for (int i = 0; i < e; i++) from[i] = rnd.nextInt(v);
            for (int i = 0; i < e; i++) dst[i] = rnd.nextInt(v);
            for (int i = 0; i < e; i++) wt[i] = 1 + rnd.nextInt(100);
            first = new int[v + 1]; to = new int[e]; w = new int[e];
            for (int f : from) first[f + 1]++;
            for (int i = 0; i < v; i++) first[i + 1] += first[i];
            int[] next = first.clone();
            for (int i = 0; i < e; i++) { int k = next[from[i]]++; to[k] = dst[i]; w[k] = wt[i]; }
        }
    }

    // Lazy Dijkstra. Each queue entry packs (dist, vertex) into one long: dist in the high 32 bits.
    static int[] dijkstra(Graph g, int s) {
        int[] dist = new int[g.v];
        Arrays.fill(dist, Integer.MAX_VALUE);
        boolean[] settled = new boolean[g.v];
        PriorityQueue<Long> pq = new PriorityQueue<>();
        dist[s] = 0;
        pq.add((long) s);
        while (!pq.isEmpty()) {
            long top = pq.poll();
            int v = (int) (top & 0xFFFFFFFFL);
            if (settled[v]) continue;
            settled[v] = true;
            for (int k = g.first[v]; k < g.first[v + 1]; k++) {
                int nd = dist[v] + g.w[k], x = g.to[k];
                if (!settled[x] && nd < dist[x]) { dist[x] = nd; pq.add(((long) nd << 32) | x); }
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        long sink = 0;
        double prev = 0;
        for (int v : new int[] {125_000, 250_000, 500_000, 1_000_000}) {
            Graph g = new Graph(v, 49);
            double best = Double.MAX_VALUE;
            for (int run = 0; run < 4; run++) {                // run 0 is the warm-up
                long t0 = System.nanoTime();
                for (int d : dijkstra(g, 0)) if (d != Integer.MAX_VALUE) sink += d;
                double ms = (System.nanoTime() - t0) / 1e6;
                if (run > 0) best = Math.min(best, ms);
            }
            String ratio = prev > 0 ? String.format("  (x%.2f)", best / prev) : "";
            System.out.println(String.format("V=%,9d  E=%,9d  %7.1f ms%s", v, 4 * v, best, ratio));
            prev = best;
        }
        System.out.println("sink " + sink);
    }
}
