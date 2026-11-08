import java.util.*;
import java.util.function.IntFunction;

public class UnionFind {
    interface UF {
        int find(int p);                           // the name of p's component
        void union(int p, int q);                  // merge p's and q's components
        default boolean connected(int p, int q) { return find(p) == find(q); }
    }

    // 1. Quick-find: id[p] IS the component name. find is O(1), union rewrites the array: O(n).
    static class QuickFind implements UF {
        private final int[] id;
        QuickFind(int n) { id = new int[n]; for (int i = 0; i < n; i++) id[i] = i; }
        public int find(int p) { return id[p]; }
        public void union(int p, int q) {
            int pid = id[p], qid = id[q];
            if (pid == qid) return;
            for (int i = 0; i < id.length; i++) if (id[i] == pid) id[i] = qid;
        }
    }

    // 2. Quick-union: parent links form trees; the root names the component.
    static class QuickUnion implements UF {
        protected final int[] parent;
        QuickUnion(int n) { parent = new int[n]; for (int i = 0; i < n; i++) parent[i] = i; }
        public int find(int p) {
            int r = p;
            while (r != parent[r]) r = parent[r];
            return r;
        }
        public void union(int p, int q) { parent[find(p)] = find(q); }
        int height() {                             // longest path to a root, with memo: O(n)
            int[] depth = new int[parent.length];
            Arrays.fill(depth, -1);
            int max = 0;
            for (int p = 0; p < parent.length; p++) {
                int r = p, steps = 0;
                while (r != parent[r] && depth[r] < 0) { r = parent[r]; steps++; }
                int base = r == parent[r] ? 0 : depth[r];
                for (int x = p, k = steps; k > 0; x = parent[x], k--) depth[x] = base + k;
                max = Math.max(max, base + steps);
            }
            return max;
        }
    }

    // 3. Weighted quick-union: hang the SMALLER tree under the larger. Optional path compression.
    static class WeightedQU extends QuickUnion {
        private final int[] size;
        private final boolean compress;
        WeightedQU(int n, boolean compress) {
            super(n);
            this.compress = compress;
            size = new int[n];
            Arrays.fill(size, 1);
        }
        @Override public int find(int p) {
            int root = super.find(p);
            if (compress) {                        // second pass: point every node on the path at the root
                int x = p;
                while (x != root) { int next = parent[x]; parent[x] = root; x = next; }
            }
            return root;
        }
        @Override public void union(int p, int q) {
            int rp = find(p), rq = find(q);
            if (rp == rq) return;
            if (size[rp] < size[rq]) { parent[rp] = rq; size[rq] += size[rp]; }
            else { parent[rq] = rp; size[rp] += size[rq]; }
        }
    }

    public static void main(String[] args) {
        int n = 1 << 16;                           // 65,536 sites, lg n = 16
        // Worst input for plain quick-union: a chain, 0-1, 1-2, 2-3, ...
        QuickUnion qu = new QuickUnion(n);
        WeightedQU wqu = new WeightedQU(n, false);
        for (int i = 0; i < n - 1; i++) { qu.union(i, i + 1); wqu.union(i, i + 1); }
        System.out.println("chain unions:   quick-union height " + qu.height() + ", weighted height " + wqu.height());
        // Worst input for weighted: always merge two trees of equal size.
        WeightedQU w2 = new WeightedQU(n, false), pc = new WeightedQU(n, true);
        for (int step = 1; step < n; step *= 2)
            for (int i = 0; i < n; i += 2 * step) { w2.union(i, i + step); pc.union(i, i + step); }
        System.out.println("equal merges:   weighted height " + w2.height() + " (lg n = 16)");
        for (int i = 0; i < n; i++) pc.find(i);
        System.out.println("  ...with path compression, after one find per site: height " + pc.height());

        // Doubling test: n random unions, then n connected() queries.
        int sink = 0;
        Map<String, IntFunction<UF>> impls = new LinkedHashMap<>();
        impls.put("quick-find", QuickFind::new);
        impls.put("quick-union", QuickUnion::new);
        impls.put("weighted+compress", size -> new WeightedQU(size, true));
        int[] sizes = {10_000, 20_000, 40_000, 80_000, 160_000};
        StringBuilder head = new StringBuilder(String.format("%-18s", "ms (ratio)"));
        for (int size : sizes) head.append(String.format("%14s", String.format("n=%,d", size)));
        System.out.println(head);
        for (var entry : impls.entrySet()) {
            StringBuilder row = new StringBuilder(String.format("%-18s", entry.getKey()));
            double prev = 0;
            for (int size : sizes) {
                if (entry.getKey().equals("quick-find") && size > 40_000 || entry.getKey().equals("quick-union") && size > 80_000) { row.append(String.format("%14s", "-")); continue; }
                double best = Double.MAX_VALUE;
                for (int run = 0; run < 4; run++) {    // run 0 is the warm-up
                    Random rnd = new Random(49);
                    UF uf = entry.getValue().apply(size);
                    long t0 = System.nanoTime();
                    for (int i = 0; i < size; i++) uf.union(rnd.nextInt(size), rnd.nextInt(size));
                    for (int i = 0; i < size; i++) if (uf.connected(rnd.nextInt(size), rnd.nextInt(size))) sink++;
                    double ms = (System.nanoTime() - t0) / 1e6;
                    if (run > 0) best = Math.min(best, ms);
                }
                row.append(String.format("%14s", prev > 0 ? String.format("%.1f (%.1f)", best, best / prev) : String.format("%.1f", best)));
                prev = best;
            }
            System.out.println(row);
        }
        System.out.println("sink " + sink);
    }
}
