// Compile together with MyHashMap.kt (Part 2):
//   kotlinc MyHashMap.kt Timing.kt -include-runtime -d t.jar && java -cp t.jar TimingKt
data class GoodKey(val id: Int)
data class BadKey(val id: Int) { override fun hashCode() = 42 }      // legal, terrible
data class BadComparableKey(val id: Int) : Comparable<BadComparableKey> {
    override fun hashCode() = 42
    override fun compareTo(other: BadComparableKey) = id.compareTo(other.id)
}

// Insert n keys, then look each one up. Returns milliseconds.
fun <K : Any> timeMine(n: Int, key: (Int) -> K): Long {
    val start = System.nanoTime()
    val m = MyHashMap<K, Int>()
    for (i in 0 until n) m[key(i)] = i
    for (i in 0 until n) check(m[key(i)] == i)
    return (System.nanoTime() - start) / 1_000_000
}

fun <K> timeJdk(n: Int, key: (Int) -> K): Long {
    val start = System.nanoTime()
    val m = HashMap<K, Int>()
    for (i in 0 until n) m[key(i)] = i
    for (i in 0 until n) check(m[key(i)] == i)
    return (System.nanoTime() - start) / 1_000_000
}

fun main() {
    repeat(3) { timeMine(4000, ::BadKey); timeJdk(4000, ::BadKey) }   // JIT warm-up
    println("milliseconds to put n keys, then get each one")
    println("%6s %9s %9s %9s %9s %12s".format("n", "mine/good", "mine/bad", "jdk/good", "jdk/bad", "jdk/bad+cmp"))
    for (n in listOf(2_000, 4_000, 8_000, 16_000)) {
        println("%6d %9d %9d %9d %9d %12d".format(n,
            timeMine(n, ::GoodKey), timeMine(n, ::BadKey),
            timeJdk(n, ::GoodKey), timeJdk(n, ::BadKey), timeJdk(n, ::BadComparableKey)))
    }
}
