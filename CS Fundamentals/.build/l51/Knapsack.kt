import java.util.Random
import kotlin.time.measureTime

// 0/1 knapsack: pick items (each at most once) with total weight <= capacity, maximising value.
fun bruteForce(w: IntArray, v: IntArray, cap: Int): Int {        // every subset: 2^n
    var best = 0
    for (mask in 0 until (1 shl w.size)) {
        var weight = 0; var value = 0
        for (i in w.indices) if (mask and (1 shl i) != 0) { weight += w[i]; value += v[i] }
        if (weight <= cap && value > best) best = value
    }
    return best
}

fun dp(w: IntArray, v: IntArray, cap: Int): Int {                // L21: best[c] = best value within capacity c
    val best = IntArray(cap + 1)
    for (i in w.indices) for (c in cap downTo w[i]) best[c] = maxOf(best[c], best[c - w[i]] + v[i])
    return best[cap]                                              // downTo: each item used at most once
}

fun main() {
    val rnd = Random(51)
    val n = 24
    val w = IntArray(n) { 1 + rnd.nextInt(100) }
    val v = IntArray(n) { 1 + rnd.nextInt(100) }
    val cap = 500
    repeat(3) { bruteForce(w.copyOf(16), v.copyOf(16), cap); dp(w, v, cap) }   // warm-up
    var a = 0; var b = 0
    val tb = measureTime { a = bruteForce(w, v, cap) }
    val td = measureTime { b = dp(w, v, cap) }
    println("n=$n cap=$cap  brute force=$a in ${tb.inWholeMilliseconds} ms (2^$n = ${1 shl n} subsets)")
    println("n=$n cap=$cap  dp=$b in ${td.inWholeMicroseconds} µs (${n.toLong() * (cap + 1)} updates at most)")
    // The same items weighed in milligrams instead of kilograms: same problem, a million times the table
    val cells = n.toLong() * (cap * 1_000_000L + 1)
    println("weights x 1,000,000: the dp needs up to $cells updates and a ${(cap * 1_000_000L + 1) * 4 / 1_000_000} MB array")
}
