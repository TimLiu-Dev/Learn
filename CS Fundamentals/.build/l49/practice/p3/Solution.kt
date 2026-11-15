class DisjointSets(n: Int) {
    val parent = IntArray(n) { it }           // public so the checks can inspect the trees
    private val size = IntArray(n) { 1 }      // size[r] is only meaningful when r is a root

    fun find(p: Int): Int {
        var root = p
        while (root != parent[root]) root = parent[root]
        var x = p                             // second pass: compress the whole path
        while (x != root) { val next = parent[x]; parent[x] = root; x = next }
        return root
    }

    // Returns false if p and q were already connected.
    fun union(p: Int, q: Int): Boolean {
        val rp = find(p); val rq = find(q)    // link roots, never p and q themselves
        if (rp == rq) return false
        if (size[rp] < size[rq]) { parent[rp] = rq; size[rq] += size[rp] }
        else { parent[rq] = rp; size[rp] += size[rq] }
        return true
    }

    fun sizeOf(p: Int): Int = size[find(p)]
}
