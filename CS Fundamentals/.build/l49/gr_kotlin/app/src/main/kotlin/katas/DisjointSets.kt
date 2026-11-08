package katas

// Weighted quick-union with path compression (Part 3).
class DisjointSets(n: Int) {
    private val parent = IntArray(n) { it }
    private val size = IntArray(n) { 1 }

    fun find(p: Int): Int {
        var root = p
        while (root != parent[root]) root = parent[root]
        var x = p
        while (x != root) { val next = parent[x]; parent[x] = root; x = next }
        return root
    }

    // Returns false if p and q were already connected.
    fun union(p: Int, q: Int): Boolean {
        val rp = find(p)
        val rq = find(q)
        if (rp == rq) return false
        if (size[rp] < size[rq]) { parent[rp] = rq; size[rq] += size[rp] }
        else { parent[rq] = rp; size[rp] += size[rq] }
        return true
    }
}
