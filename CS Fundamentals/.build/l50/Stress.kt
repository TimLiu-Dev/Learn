import kotlin.random.Random

// Practise without an online judge: check a fast solution against an obviously correct slow one
// on thousands of small random inputs, and print the first input where they disagree.
fun <T, R> stress(trials: Int, gen: (Random, Int) -> T, slow: (T) -> R, fast: (T) -> R, show: (T) -> String): Boolean {
    val rnd = Random(50)
    for (t in 0 until trials) {
        val input = gen(rnd, 1 + t / 1000)               // small inputs first: counterexamples stay readable
        val want = slow(input)
        val got = fast(input)
        if (want != got) {
            println("trial $t: ${show(input)}  slow=$want fast=$got")
            return false
        }
    }
    println("$trials trials, no difference")
    return true
}

data class Case(val a: IntArray, val target: Int)

fun shortestSlow(c: Case): Int {                         // try every start and end: O(n^2), easy to trust
    var best = 0
    for (i in c.a.indices) {
        var sum = 0
        for (j in i until c.a.size) {
            sum += c.a[j]
            if (sum >= c.target) { if (best == 0 || j - i + 1 < best) best = j - i + 1; break }
        }
    }
    return best
}

fun shortestBuggy(c: Case): Int {                        // `if` where the window needs `while`
    var left = 0; var sum = 0; var best = Int.MAX_VALUE
    for (right in c.a.indices) {
        sum += c.a[right]
        if (sum >= c.target) { best = minOf(best, right - left + 1); sum -= c.a[left++] }
    }
    return if (best == Int.MAX_VALUE) 0 else best
}

fun shortestFixed(c: Case): Int {
    var left = 0; var sum = 0; var best = Int.MAX_VALUE
    for (right in c.a.indices) {
        sum += c.a[right]
        while (sum >= c.target) { best = minOf(best, right - left + 1); sum -= c.a[left++] }
    }
    return if (best == Int.MAX_VALUE) 0 else best
}

fun main() {
    val gen = { rnd: Random, size: Int -> Case(IntArray(rnd.nextInt(1, size + 4)) { rnd.nextInt(1, 6) }, rnd.nextInt(1, 15)) }
    val show = { c: Case -> "a=${c.a.contentToString()} target=${c.target}" }
    stress(10_000, gen, ::shortestSlow, ::shortestBuggy, show)
    stress(10_000, gen, ::shortestSlow, ::shortestFixed, show)
}
