fun main() {
    val parent = IntArray(4) { it }
    fun find(p: Int): Int {
        var r = p
        while (r != parent[r]) r = parent[r]
        return r
    }
    fun union(p: Int, q: Int) { parent[p] = q }      // links p itself, not p's root

    union(0, 1)
    union(0, 2)
    println("${find(1) == find(2)} ${find(0) == find(2)}")
}
