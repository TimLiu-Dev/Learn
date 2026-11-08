import java.util.Random
import kotlin.math.abs
import kotlin.time.measureTime

// CNF: an AND of clauses, each an OR of literals. Literal +v means x_v, -v means NOT x_v (v = 1..n).
// An assignment is a Long whose bit v-1 holds the value of x_v (L48).
fun satisfies(formula: List<IntArray>, a: Long): Boolean {     // the verifier: O(size of formula)
    for (clause in formula) {
        var ok = false
        for (lit in clause) {
            val bit = (a shr (abs(lit) - 1)) and 1L == 1L
            if (bit == (lit > 0)) { ok = true; break }
        }
        if (!ok) return false
    }
    return true
}

fun randomFormula(n: Int, m: Int, rnd: Random): List<IntArray> = List(m) {
    val vars = (1..n).shuffled(rnd).take(3)                    // 3 different variables per clause
    IntArray(3) { i -> if (rnd.nextBoolean()) vars[i] else -vars[i] }
}

var tried = 0L
fun bruteForce(n: Int, formula: List<IntArray>): Long? {       // the searcher: up to 2^n tries
    tried = 0
    for (a in 0L until (1L shl n)) { tried++; if (satisfies(formula, a)) return a }
    return null
}

fun main() {
    repeat(3) { bruteForce(12, randomFormula(12, 60, Random(it.toLong()))) }   // warm-up
    var rate = 0.0
    for (n in 16..24 step 2) {
        val f = randomFormula(n, 5 * n, Random(n.toLong()))
        var answer: Long? = null
        val t = measureTime { answer = bruteForce(n, f) }
        rate = tried / (t.inWholeMicroseconds / 1e6)
        println("n=%2d clauses=%3d  %-5s tried %,10d of %,10d  %7d ms".format(
            n, f.size, if (answer == null) "UNSAT" else "SAT", tried, 1L shl n, t.inWholeMilliseconds))
    }
    // Checking a proposed answer is cheap even when finding one isn't
    val rnd = Random(60)
    val secret = rnd.nextLong() ushr 4                          // a 60-variable assignment
    val f = randomFormula(60, 300, rnd).map { c ->             // make every clause agree with it
        if (satisfies(listOf(c), secret)) c else c.also { it[0] = -it[0] } }
    repeat(1000) { satisfies(f, secret) }                      // warm-up
    val check = measureTime { satisfies(f, secret) }
    println("n=60 clauses=300: checking the answer took %.1f µs".format(check.inWholeNanoseconds / 1000.0))
    println("brute force at %.0f million tries/s would need ~%,.0f years".format(
        rate / 1e6, (1L shl 60) / rate / 3600 / 24 / 365))
}
