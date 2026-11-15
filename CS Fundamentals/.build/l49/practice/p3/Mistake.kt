class DisjointSets(n: Int) {
    val parent = IntArray(n) { it }
    private val size = IntArray(n) { 1 }

    // Compresses only p itself: the nodes between p and the root keep their old parents.
    fun find(p: Int): Int {
        var root = p
        while (root != parent[root]) root = parent[root]
        parent[p] = root
        return root
    }

    fun union(p: Int, q: Int): Boolean {
        val rp = find(p); val rq = find(q)
        if (rp == rq) return false
        if (size[rp] < size[rq]) { parent[rp] = rq; size[rq] += size[rp] }
        else { parent[rq] = rp; size[rp] += size[rq] }
        return true
    }

    fun sizeOf(p: Int): Int = size[find(p)]
}
