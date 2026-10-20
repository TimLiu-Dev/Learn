// 4 threads count words from L3's text into one shared map. Three versions of the map.
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.LongAdder
import kotlin.concurrent.thread

const val TEXT = "The cat sat on the mat. The mat was flat,\nand the cat was fat! A cat, a mat, a hat."
val WORDS = TEXT.lowercase().split(Regex("[^a-z]+")).filter { it.isNotEmpty() }
const val THREADS = 4
const val COPIES = 25_000                                // 100,000 copies of the text in total

fun run(add: (String) -> Unit) {
    val ts = List(THREADS) { thread { repeat(COPIES) { WORDS.forEach(add) } } }
    ts.forEach { it.join() }
}

fun report(name: String, counts: Map<String, Long>) =
    println("$name total=${counts.values.sum()} the=${counts["the"]} cat=${counts["cat"]}")

fun main() {
    val plain = HashMap<String, Long>()
    run { w -> plain.merge(w, 1L, Long::plus) }          // HashMap: not thread-safe
    report("HashMap      ", plain)

    val chm = ConcurrentHashMap<String, Long>()
    run { w -> chm.merge(w, 1L, Long::plus) }
    report("CHM merge    ", chm)

    val adders = ConcurrentHashMap<String, LongAdder>()
    run { w -> adders.computeIfAbsent(w) { LongAdder() }.increment() }
    report("CHM LongAdder", adders.mapValues { it.value.sum() })
}
