import java.util.*;

public class Kruskal {
    // The weighted quick-union with path compression from UnionFind.java, trimmed.
    static final class DisjointSets {
        private final int[] parent, size;
        DisjointSets(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
        }
        int find(int p) {
            int root = p;
            while (root != parent[root]) root = parent[root];
            for (int x = p; x != root; ) { int next = parent[x]; parent[x] = root; x = next; }
            return root;
        }
        // Returns false if p and q were already connected.
        boolean union(int p, int q) {
            int rp = find(p), rq = find(q);
            if (rp == rq) return false;
            if (size[rp] < size[rq]) { parent[rp] = rq; size[rq] += size[rp]; }
            else { parent[rq] = rp; size[rp] += size[rq]; }
            return true;
        }
    }

    record Road(String a, String b, int minutes) {}

    public static void main(String[] args) {
        List<String> towns = List.of("A", "B", "C", "D", "E", "F", "G");
        List<Road> roads = new ArrayList<>(List.of(
            new Road("A", "B", 4), new Road("A", "C", 2), new Road("B", "C", 1), new Road("B", "D", 5),
            new Road("C", "D", 8), new Road("C", "E", 10), new Road("D", "E", 2), new Road("D", "F", 6),
            new Road("E", "F", 2), new Road("F", "G", 3), new Road("E", "G", 4)));
        roads.sort(Comparator.comparingInt(Road::minutes));            // lightest first (a stable sort)
        DisjointSets sets = new DisjointSets(towns.size());
        List<Road> tree = new ArrayList<>();
        for (Road r : roads) {
            if (tree.size() == towns.size() - 1) break;                // V-1 edges: done
            if (sets.union(towns.indexOf(r.a()), towns.indexOf(r.b()))) {
                tree.add(r);
                System.out.println("take  " + r.a() + "-" + r.b() + " " + r.minutes());
            } else {
                System.out.println("skip  " + r.a() + "-" + r.b() + " " + r.minutes() + "  (would close a cycle)");
            }
        }
        System.out.println(tree.size() + " roads, total " + tree.stream().mapToInt(Road::minutes).sum() + " minutes");
    }
}
