// Prefix sums: pay O(n) once, then every range sum costs O(1).
import kotlin.random.Random

// pre[i] = a[0] + … + a[i - 1], so pre has n + 1 slots and pre[0] = 0.
fun prefixSums(a: IntArray): LongArray {
    val pre = LongArray(a.size + 1)                      // Long: a sum of many Ints can overflow (L2)
    for (i in a.indices) pre[i + 1] = pre[i] + a[i]
    return pre
}

// Sum of a[from, to), half-open like substring: two lookups, however long the range.
fun rangeSum(pre: LongArray, from: Int, to: Int): Long = pre[to] - pre[from]

// How many contiguous runs sum to exactly k? The run a[i, j) sums to k exactly when
// pre[j] - pre[i] == k, so at each j, count the earlier prefix sums equal to pre[j] - k.
fun countSubarrays(a: IntArray, k: Long): Int {
    val seen = HashMap<Long, Int>()                      // prefix sum -> how many times it has occurred
    seen[0L] = 1                                         // the empty prefix: lets a run start at index 0
    var sum = 0L
    var count = 0
    for (x in a) {
        sum += x
        count += seen[sum - k] ?: 0                      // each earlier prefix that is k smaller ends a run here
        seen[sum] = (seen[sum] ?: 0) + 1
    }
    return count
}

// The obviously correct O(n²) version: try every run. Returns the runs as half-open ranges [from, to).
fun runsBrute(a: IntArray, k: Long): List<String> {
    val runs = mutableListOf<String>()
    for (i in a.indices) {
        var sum = 0L
        for (j in i until a.size) {
            sum += a[j]
            if (sum == k) runs += "[$i, ${j + 1})"
        }
    }
    return runs
}

fun main() {
    val a = intArrayOf(3, -1, 4, 1, -5, 9, 2, -6)
    val pre = prefixSums(a)
    println("a   = ${a.contentToString()}")
    println("pre = ${pre.contentToString()}")
    check(pre.toList() == a.runningFold(0L) { s, x -> s + x })   // the standard library agrees
    println("sum of a[2, 6) = ${rangeSum(pre, 2, 6)}")
    println("runs summing to 4: ${countSubarrays(a, 4)}  ${runsBrute(a, 4)}")

    val rnd = Random(50)                                 // differential test, as in the window-maximum section
    repeat(10_000) {
        val xs = IntArray(rnd.nextInt(0, 12)) { rnd.nextInt(-3, 4) }
        val k = rnd.nextLong(-3, 4)
        check(countSubarrays(xs, k) == runsBrute(xs, k).size) { "differs on ${xs.joinToString()} k=$k" }
    }
    println("10000 random arrays with negatives: map count == brute force")
}
