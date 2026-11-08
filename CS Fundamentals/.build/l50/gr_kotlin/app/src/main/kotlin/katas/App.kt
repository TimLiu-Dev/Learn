package katas

import kotlin.time.measureTime

fun main() {
    val book = Autocomplete::class.java.getResource("/alice.txt")!!.readText()
    val body = book.substringAfter("*** START OF").substringAfter("***").substringBefore("*** END OF")

    val ac = Autocomplete()
    ac.addText(body)
    println("${ac.size} distinct words")
    for (p in listOf("th", "al", "wh", "qu", "x")) {
        println("%-3s -> %s".format(p, ac.suggest(p, 5).joinToString { "${it.word} ${it.count}" }))
    }

    // The L3 way, for comparison: a map of counts, scanned and sorted on every query.
    val counts = HashMap<String, Int>()
    for (w in body.lowercase().split(Regex("[^a-z]+"))) if (w.isNotEmpty()) counts[w] = (counts[w] ?: 0) + 1
    fun scan(p: String, k: Int) = counts.entries.filter { it.key.startsWith(p) }
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
        .take(k).map { Suggestion(it.key, it.value) }

    val prefixes = counts.keys.filter { it.length >= 2 }.map { it.take(2) }.distinct().sorted()
    check(prefixes.all { ac.suggest(it, 5) == scan(it, 5) })
    var sink = 0
    repeat(5) { prefixes.forEach { sink += scan(it, 5).size + ac.suggest(it, 5).size } }   // warm-up (L16)
    val tScan = measureTime { repeat(10) { prefixes.forEach { sink += scan(it, 5).size } } }
    val tTrie = measureTime { repeat(10) { prefixes.forEach { sink += ac.suggest(it, 5).size } } }
    val q = 10 * prefixes.size
    println("${prefixes.size} two-letter prefixes, same answers both ways")
    println("scan + sort: %.1f µs per query".format(tScan.inWholeNanoseconds / 1000.0 / q))
    println("trie + heap: %.1f µs per query".format(tTrie.inWholeNanoseconds / 1000.0 / q))
    println("(sink $sink)")
}
