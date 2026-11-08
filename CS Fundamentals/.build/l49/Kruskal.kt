// The weighted quick-union with path compression from UnionFind.kt, trimmed.
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
        val rp = find(p); val rq = find(q)
        if (rp == rq) return false
        if (size[rp] < size[rq]) { parent[rp] = rq; size[rq] += size[rp] }
        else { parent[rq] = rp; size[rp] += size[rq] }
        return true
    }
}

data class Road(val a: String, val b: String, val minutes: Int)

val towns = listOf("A", "B", "C", "D", "E", "F", "G")
val roads = listOf(
    Road("A", "B", 4), Road("A", "C", 2), Road("B", "C", 1), Road("B", "D", 5),
    Road("C", "D", 8), Road("C", "E", 10), Road("D", "E", 2), Road("D", "F", 6),
    Road("E", "F", 2), Road("F", "G", 3), Road("E", "G", 4),
)

fun main() {
    val index = towns.withIndex().associate { (i, t) -> t to i }
    val sets = DisjointSets(towns.size)
    val tree = mutableListOf<Road>()
    for (r in roads.sortedBy { it.minutes }) {                 // lightest first
        if (tree.size == towns.size - 1) break                 // V-1 edges: done
        if (sets.union(index.getValue(r.a), index.getValue(r.b))) {
            tree += r
            println("take  ${r.a}-${r.b} ${r.minutes}")
        } else {
            println("skip  ${r.a}-${r.b} ${r.minutes}  (would close a cycle)")
        }
    }
    println("${tree.size} roads, total ${tree.sumOf { it.minutes }} minutes")
}
