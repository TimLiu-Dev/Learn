import kotlin.time.measureTime

// n lookups against a collection of n numbers: List vs HashSet.
fun lookups(n: Int, contains: (Int) -> Boolean): Int {
    var found = 0
    for (i in 0 until n) if (contains(i * 2)) found++     // half the lookups hit, half miss
    return found
}

fun main() {
    var sink = 0
    println("%8s %12s %12s".format("n", "List ms", "HashSet ms"))
    for (n in listOf(5_000, 10_000, 20_000, 40_000, 80_000)) {
        val list = (0 until n).toList()                    // an ArrayList on the JVM
        val set = list.toHashSet()
        repeat(2) { sink += lookups(n) { it in list } + lookups(n) { it in set } }  // warm-up
        val tList = measureTime { sink += lookups(n) { it in list } }
        val tSet = measureTime { sink += lookups(n) { it in set } }
        println("%8d %12.2f %12.2f".format(n, tList.inWholeMicroseconds / 1e3, tSet.inWholeMicroseconds / 1e3))
    }
    println("(sink $sink)")
}
