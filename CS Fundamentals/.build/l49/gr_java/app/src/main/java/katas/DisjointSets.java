package katas;

// Weighted quick-union with path compression (Part 3).
public final class DisjointSets {
    private final int[] parent, size;

    public DisjointSets(int n) {
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
    }

    public int find(int p) {
        int root = p;
        while (root != parent[root]) root = parent[root];
        for (int x = p; x != root; ) { int next = parent[x]; parent[x] = root; x = next; }
        return root;
    }

    // Returns false if p and q were already connected.
    public boolean union(int p, int q) {
        int rp = find(p), rq = find(q);
        if (rp == rq) return false;
        if (size[rp] < size[rq]) { parent[rp] = rq; size[rq] += size[rp]; }
        else { parent[rq] = rp; size[rp] += size[rq]; }
        return true;
    }
}
