import kotlin.math.abs

// The reduction: 3-COLORING -> SAT. Variable x(v, c) means "vertex v gets colour c".
fun x(v: Int, c: Int) = 3 * v + c + 1

fun toCnf(vertices: Int, edges: List<Pair<Int, Int>>): List<IntArray> = buildList {
    for (v in 0 until vertices) {
        add(intArrayOf(x(v, 0), x(v, 1), x(v, 2)))                          // at least one colour
        for (c in 0..2) for (d in c + 1..2) add(intArrayOf(-x(v, c), -x(v, d)))  // at most one
    }
    for ((u, v) in edges) for (c in 0..2) add(intArrayOf(-x(u, c), -x(v, c)))   // neighbours differ
}

// A backtracking SAT solver (L50): set x1, x2, ... in turn, and abandon a branch
// as soon as some clause has all of its literals false.
var nodes = 0
fun solve(n: Int, f: List<IntArray>): IntArray? {
    val value = IntArray(n + 1)                         // 0 = unset, 1 = true, -1 = false
    fun falsified(clause: IntArray) = clause.all { lit -> value[abs(lit)] == (if (lit > 0) -1 else 1) }
    fun go(v: Int): Boolean {
        nodes++
        if (f.any(::falsified)) return false            // prune
        if (v > n) return true
        for (choice in intArrayOf(1, -1)) { value[v] = choice; if (go(v + 1)) return true }
        value[v] = 0
        return false
    }
    nodes = 0
    return if (go(1)) value else null
}

fun wheel(rim: Int) = (1..rim).flatMap { listOf(0 to it, it to it % rim + 1) }   // hub 0, rim 1..rim
val petersen = (0..4).flatMap { listOf(it to (it + 1) % 5, it to it + 5, it + 5 to (it + 2) % 5 + 5) }

fun main() {
    val graphs = listOf(Triple("wheel, 5-cycle rim", 6, wheel(5)), Triple("wheel, 6-cycle rim", 7, wheel(6)),
        Triple("Petersen graph", 10, petersen))
    for ((name, vertices, edges) in graphs) {
        val f = toCnf(vertices, edges)
        val value = solve(3 * vertices, f)
        print("%-20s V=%2d E=%2d -> %2d vars, %3d clauses: ".format(name, vertices, edges.size, 3 * vertices, f.size))
        if (value == null) { println("UNSAT, so no 3-colouring ($nodes nodes)"); continue }
        val colour = IntArray(vertices) { v -> (0..2).first { value[x(v, it)] == 1 } }   // decode
        val proper = edges.all { (u, v) -> colour[u] != colour[v] }
        println("SAT ${colour.joinToString("")} proper=$proper ($nodes nodes)")
    }
}
