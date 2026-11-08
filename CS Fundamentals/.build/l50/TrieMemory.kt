import java.io.File
import kotlin.time.measureTime

// Prefix search over every distinct word in Alice's Adventures in Wonderland, three ways.
// Compile together with Trie.kt:  kotlinc Trie.kt TrieMemory.kt -include-runtime -d tm.jar
// Run (both files have a main):   java -cp tm.jar TrieMemoryKt

fun bookWords(path: String): List<String> {
    val text = File(path).readText()
    val body = text.substringAfter("*** START OF").substringAfter("***").substringBefore("*** END OF")
    return body.lowercase().split(Regex("[^a-z]+")).filter { it.isNotEmpty() }   // L3's rule for a word
}

fun usedMb(): Double {
    repeat(3) { System.gc(); Thread.sleep(50) }
    val rt = Runtime.getRuntime()
    return (rt.totalMemory() - rt.freeMemory()) / 1_048_576.0
}

fun main() {
    val all = bookWords("data/alice.txt")
    val distinct = all.toSortedSet().toList()
    println("${all.size} words, ${distinct.size} distinct, ${distinct.sumOf { it.length }} letters in the distinct words")

    val base = usedMb()
    val trie = Trie()
    distinct.forEach(trie::insert)
    val trieMb = usedMb() - base
    println("trie:       ${trie.nodes} nodes, about %.1f MB".format(trieMb))

    val base2 = usedMb()
    val byPrefix = HashMap<String, MutableList<String>>()   // every prefix -> every word that starts with it
    for (w in distinct) for (end in 1..w.length) byPrefix.getOrPut(w.substring(0, end)) { mutableListOf() } += w
    val mapMb = usedMb() - base2
    println("prefix map: ${byPrefix.size} keys holding ${byPrefix.keys.sumOf { it.length }} chars, ${byPrefix.values.sumOf { it.size }} list slots, about %.1f MB".format(mapMb))

    // Same answers?
    for (p in listOf("th", "alic", "q", "zz")) {
        val scan = distinct.filter { it.startsWith(p) }
        check(trie.withPrefix(p) == scan && (byPrefix[p] ?: emptyList<String>()) == scan)
    }
    println("withPrefix(\"alic\") = ${trie.withPrefix("alic")}")

    // Time 2,000 lookups of 3-letter prefixes taken from the words themselves.
    val prefixes = distinct.filter { it.length >= 3 }.map { it.take(3) }.shuffled(kotlin.random.Random(50)).take(2_000)
    var sink = 0L
    fun timeIt(name: String, f: (String) -> List<String>) {
        repeat(3) { prefixes.forEach { sink += f(it).size } }                  // warm-up (L16)
        val d = measureTime { prefixes.forEach { sink += f(it).size } }
        println("%-12s %8.2f µs per query".format(name, d.inWholeNanoseconds / 1000.0 / prefixes.size))
    }
    timeIt("scan list") { p -> distinct.filter { it.startsWith(p) } }
    timeIt("trie") { p -> trie.withPrefix(p) }
    timeIt("prefix map") { p -> byPrefix[p] ?: emptyList() }
    println("(sink $sink)")
}
