import kotlin.random.Random
import kotlin.time.measureTime

// Sum the "big" values (>= 128) of 2,097,152 random bytes, the same loop over the same numbers,
// once in random order and once sorted. Only the order changes, so only the branch changes.
fun sumBig(data: IntArray): Long {
    var s = 0L
    for (x in data) if (x >= 128) s += x
    return s
}

// No branch at all: build a mask from the sign bit (L48). (x - 128) shr 31 is -1 (all ones)
// when x < 128 and 0 otherwise, so inv() of it keeps x only when x >= 128.
fun sumBigBranchless(data: IntArray): Long {
    var s = 0L
    for (x in data) s += ((x - 128) shr 31).inv() and x
    return s
}

fun bestOf(data: IntArray, f: (IntArray) -> Long): Pair<Double, Long> {
    var sink = 0L
    var best = Double.MAX_VALUE
    repeat(7) {
        val t = measureTime { repeat(20) { sink += f(data) } }
        best = minOf(best, t.inWholeNanoseconds / 20.0 / data.size)
    }
    return Pair(best, sink)
}

fun main() {
    val rng = Random(42)                                  // ONE generator for the whole array
    val random = IntArray(2_097_152) { rng.nextInt(256) }
    val sorted = random.sortedArray()
    for (data in listOf(random, sorted)) repeat(3) { sumBig(data); sumBigBranchless(data) }   // warm up (L16)
    val cases = listOf(
        "if, random order " to Pair(random, ::sumBig),
        "if, sorted       " to Pair(sorted, ::sumBig),
        "mask, random     " to Pair(random, ::sumBigBranchless),
        "mask, sorted     " to Pair(sorted, ::sumBigBranchless),
    )
    for ((name, c) in cases) {
        val (ns, sink) = bestOf(c.first, c.second)
        println("$name %.2f ns per element   (sink $sink)".format(ns))
    }
}
