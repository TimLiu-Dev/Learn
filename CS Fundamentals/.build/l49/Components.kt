import java.io.File

fun main() {
    val lines = File("tinyUF.txt").readLines().filter { it.isNotBlank() }
    val n = lines[0].trim().toInt()
    val parent = IntArray(n) { it }
    val size = IntArray(n) { 1 }
    fun find(p: Int): Int {
        var r = p
        while (r != parent[r]) { parent[r] = parent[parent[r]]; r = parent[r] }   // path halving
        return r
    }
    var components = n                                   // every site starts alone
    for (line in lines.drop(1)) {
        val (p, q) = line.trim().split(Regex("\\s+")).map { it.toInt() }
        val rp = find(p); val rq = find(q)
        if (rp == rq) continue                           // already connected: ignore the pair
        if (size[rp] < size[rq]) { parent[rp] = rq; size[rq] += size[rp] }
        else { parent[rq] = rp; size[rp] += size[rq] }
        components--                                     // each real union merges two into one
        println("$p $q")
    }
    println("$components components")
}
