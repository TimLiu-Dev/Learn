import java.util.LinkedList
import kotlin.random.Random

fun <T> time(label: String, block: () -> T) {
    val t0 = System.nanoTime()
    val result = block()
    println("%-34s %8.1f ms   -> %s".format(label, (System.nanoTime() - t0) / 1e6, result))
}

fun main() {
    val rnd = Random(42)
    val a = List(20_000) { rnd.nextInt(1_000_000) }
    val b = List(20_000) { rnd.nextInt(1_000_000) }

    repeat(2) { round ->                     // round 0 is a JIT warm-up; read round 1
        println("-- round $round")
        time("common: List.contains in a loop") { b.count { it in a } }
        time("common: HashSet") { val set = a.toHashSet(); b.count { it in set } }

        val linked = LinkedList(a)
        time("LinkedList.get(i) in a for loop") { var s = 0L; for (i in linked.indices) s += linked[i]; s }
        time("LinkedList for-each") { var s = 0L; for (x in linked) s += x; s }

        val big = List(1_000_000) { rnd.nextInt() }
        time("min by sorting") { big.sorted().first() }
        time("min with one pass") { big.min() }
    }
}
