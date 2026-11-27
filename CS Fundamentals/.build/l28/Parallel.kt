import java.util.LinkedList
import java.util.stream.IntStream
import java.util.stream.Stream

fun prime(n: Int): Boolean {
    if (n < 2) return false
    var d = 2
    while (d * d <= n) { if (n % d == 0) return false; d++ }
    return true
}

fun ms(times: Int, block: () -> Long): Long {
    val t = System.nanoTime()
    repeat(times) { check(block() > 0) }                         // use the result so it can't be skipped
    return (System.nanoTime() - t) / 1_000_000
}

// Light work (square and sum, 20 times) and heavy work (a prime test, once) on one source.
fun row(name: String, source: () -> Stream<Int>): String {
    val light = ms(20) { source().mapToLong { it.toLong() * it }.sum() }
    val heavy = ms(1) { source().filter(::prime).count() }
    return "%-14s light %4d ms   heavy %4d ms".format(name, light, heavy)
}

fun main() {
    // 1. Who runs a parallel stream? The calling thread plus common-pool workers.
    val names = IntStream.range(0, 1_000).parallel()
        .mapToObj { Thread.currentThread().name.substringBefore("-worker") }.distinct().sorted().toList()
    println("threads: $names")

    // 2. The same 5,000,000 numbers from three sources, sequential and parallel.
    val n = 5_000_000
    val array = ArrayList((0 until n).toList())
    val linked = LinkedList(array)
    val iterate = { Stream.iterate(0) { it + 1 }.limit(n.toLong()) }
    repeat(3) { round ->                                         // rounds 0 and 1 are warm-up
        val rows = listOf(
            row("ArrayList seq") { array.stream() }, row("ArrayList par") { array.parallelStream() },
            row("LinkedList seq") { linked.stream() }, row("LinkedList par") { linked.parallelStream() },
            row("iterate seq") { iterate() }, row("iterate par") { iterate().parallel() },
        )
        if (round == 2) rows.forEach(::println)
    }

    // 3. Side effects: a plain ArrayList filled from a parallel forEach.
    repeat(3) {
        val out = ArrayList<Int>()
        val result = try { array.parallelStream().forEach { out.add(it) }; "size ${out.size}" }
                     catch (e: Exception) { e.javaClass.simpleName }
        println("forEach into ArrayList: $result (expected $n)")
    }
    println("toList():               size ${array.parallelStream().toList().size}")

    // 4. Order: forEach ignores encounter order, forEachOrdered keeps it.
    val seen = StringBuilder()
    (1..12).toList().parallelStream().forEach { synchronized(seen) { seen.append("$it ") } }
    println("forEach:        $seen")
    val ordered = StringBuilder()
    (1..12).toList().parallelStream().forEachOrdered { ordered.append(it).append(' ') }
    println("forEachOrdered: $ordered")
}
