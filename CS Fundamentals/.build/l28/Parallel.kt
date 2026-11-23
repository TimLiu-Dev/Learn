import java.util.LinkedList
import java.util.stream.IntStream
import java.util.stream.Stream

fun prime(n: Int): Boolean {
    if (n < 2) return false
    var d = 2
    while (d * d <= n) { if (n % d == 0) return false; d++ }
    return true
}

fun ms(block: () -> Any): String {
    val t = System.nanoTime(); val r = block()
    return "$r in ${(System.nanoTime() - t) / 1_000_000} ms"
}

fun main() {
    // 1. Who runs a parallel stream? The caller plus common-pool workers.
    val names = IntStream.range(0, 1_000).parallel()
        .mapToObj { Thread.currentThread().name.substringBefore("-worker") }.distinct().sorted().toList()
    println("threads: $names")

    // 2. Same pipeline, three sources of the same 2,000,000 numbers.
    val n = 2_000_000
    val array = ArrayList((0 until n).toList())
    val linked = LinkedList(array)
    repeat(2) { round ->                                       // round 0 is warm-up
        val lines = listOf(
            "ArrayList  seq " + ms { array.stream().filter(::prime).count() },
            "ArrayList  par " + ms { array.parallelStream().filter(::prime).count() },
            "LinkedList par " + ms { linked.parallelStream().filter(::prime).count() },
            "iterate    par " + ms { Stream.iterate(0) { it + 1 }.parallel().limit(n.toLong()).filter(::prime).count() },
        )
        if (round == 1) lines.forEach(::println)
    }

    // 3. Side effects: a plain ArrayList filled from a parallel forEach.
    repeat(3) {
        val out = ArrayList<Int>()
        val result = try { array.parallelStream().forEach { out.add(it) }; "size ${out.size}" }
                     catch (e: Exception) { e.javaClass.simpleName }
        println("forEach into ArrayList: $result (expected $n)")
    }
    println("toList(): size ${array.parallelStream().toList().size}")

    // 4. Order: forEach ignores encounter order, forEachOrdered keeps it.
    val sb = StringBuilder()
    (1..12).toList().parallelStream().forEach { synchronized(sb) { sb.append(it).append(' ') } }
    println("forEach:        $sb")
    val ordered = StringBuilder()
    (1..12).toList().parallelStream().forEachOrdered { ordered.append(it).append(' ') }
    println("forEachOrdered: $ordered")
}
