import java.util.Random

interface UF {
    fun find(p: Int): Int                      // the name of p's component
    fun union(p: Int, q: Int)                  // merge p's and q's components
    fun connected(p: Int, q: Int) = find(p) == find(q)
}

// 1. Quick-find: id[p] IS the component name. find is O(1), union rewrites the array: O(n).
class QuickFind(n: Int) : UF {
    private val id = IntArray(n) { it }
    override fun find(p: Int) = id[p]
    override fun union(p: Int, q: Int) {
        val pid = id[p]; val qid = id[q]
        if (pid == qid) return
        for (i in id.indices) if (id[i] == pid) id[i] = qid
    }
}

// 2. Quick-union: parent links form trees; the root names the component.
open class QuickUnion(n: Int) : UF {
    protected val parent = IntArray(n) { it }
    override fun find(p: Int): Int {
        var r = p
        while (r != parent[r]) r = parent[r]
        return r
    }
    override fun union(p: Int, q: Int) { parent[find(p)] = find(q) }
    fun height(): Int {                        // longest path to a root, with memo: O(n)
        val depth = IntArray(parent.size) { -1 }
        fun d(p: Int): Int {
            var r = p; var steps = 0
            while (r != parent[r] && depth[r] < 0) { r = parent[r]; steps++ }
            val base = if (r == parent[r]) 0 else depth[r]
            var x = p; var k = steps
            while (k > 0) { depth[x] = base + k; x = parent[x]; k-- }
            return base + steps
        }
        return parent.indices.maxOf { d(it) }
    }
}

// 3. Weighted quick-union: hang the SMALLER tree under the larger. Optional path compression.
class WeightedQU(n: Int, private val compress: Boolean) : QuickUnion(n) {
    private val size = IntArray(n) { 1 }
    override fun find(p: Int): Int {
        val root = super.find(p)
        if (compress) {                        // second pass: point every node on the path at the root
            var x = p
            while (x != root) { val next = parent[x]; parent[x] = root; x = next }
        }
        return root
    }
    override fun union(p: Int, q: Int) {
        val rp = find(p); val rq = find(q)
        if (rp == rq) return
        if (size[rp] < size[rq]) { parent[rp] = rq; size[rq] += size[rp] }
        else { parent[rq] = rp; size[rp] += size[rq] }
    }
}

fun main() {
    val n = 1 shl 16                           // 65,536 sites, lg n = 16
    // Worst input for plain quick-union: a chain, 0-1, 1-2, 2-3, ...
    val qu = QuickUnion(n); val wqu = WeightedQU(n, false)
    for (i in 0 until n - 1) { qu.union(i, i + 1); wqu.union(i, i + 1) }
    println("chain unions:   quick-union height ${qu.height()}, weighted height ${wqu.height()}")
    // Worst input for weighted: always merge two trees of equal size.
    val w2 = WeightedQU(n, false); val pc = WeightedQU(n, true)
    var step = 1
    while (step < n) {
        for (i in 0 until n step 2 * step) { w2.union(i, i + step); pc.union(i, i + step) }
        step *= 2
    }
    println("equal merges:   weighted height ${w2.height()} (lg n = 16)")
    for (i in 0 until n) pc.find(i)
    println("  ...with path compression, after one find per site: height ${pc.height()}")

    // Doubling test: n random unions, then n connected() queries.
    var sink = 0
    val impls = linkedMapOf<String, (Int) -> UF>(
        "quick-find" to { QuickFind(it) }, "quick-union" to { QuickUnion(it) },
        "weighted+compress" to { WeightedQU(it, true) })
    val sizes = listOf(10_000, 20_000, 40_000, 80_000, 160_000)
    println("%-18s".format("ms (ratio)") + sizes.joinToString("") { "%14s".format("n=%,d".format(it)) })
    for ((name, make) in impls) {
        val row = StringBuilder("%-18s".format(name))
        var prev = 0.0
        for (size in sizes) {
            if (name == "quick-find" && size > 40_000 || name == "quick-union" && size > 80_000) { row.append("%14s".format("-")); continue }
            var best = Double.MAX_VALUE
            repeat(4) { run ->                 // run 0 is the warm-up
                val rnd = Random(49)
                val uf = make(size)
                val t0 = System.nanoTime()
                repeat(size) { uf.union(rnd.nextInt(size), rnd.nextInt(size)) }
                repeat(size) { if (uf.connected(rnd.nextInt(size), rnd.nextInt(size))) sink++ }
                val ms = (System.nanoTime() - t0) / 1e6
                if (run > 0) best = minOf(best, ms)
            }
            row.append("%14s".format(if (prev > 0) "%.1f (%.1f)".format(best, best / prev) else "%.1f".format(best)))
            prev = best
        }
        println(row)
    }
    println("sink $sink")
}
