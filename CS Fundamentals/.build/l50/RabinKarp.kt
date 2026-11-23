// Rabin–Karp: a sliding window over a hash. Each slide updates the window's hash in O(1)
// instead of rehashing m characters. The hash is String.hashCode's polynomial,
// s[0]·31^(m-1) + … + s[m-1], in Int arithmetic, so overflow wraps (mod 2^32) as in Java.
import java.io.File

class Result(val matches: List<Int>, val hashHits: Int)

fun search(text: String, pat: String): Result {
    val m = pat.length
    if (m == 0 || m > text.length) return Result(emptyList(), 0)
    var high = 1                                         // 31^(m-1): the weight of the char that leaves
    repeat(m - 1) { high *= 31 }
    val target = pat.hashCode()
    var h = 0
    for (i in 0 until m) h = 31 * h + text[i].code       // hash of the first window (Horner's rule)
    val matches = mutableListOf<Int>()
    var hits = 0
    for (i in 0..text.length - m) {
        if (i > 0) h = 31 * (h - text[i - 1].code * high) + text[i + m - 1].code   // drop left, add right
        if (h == target) {
            hits++
            if (text.regionMatches(i, pat, 0, m)) matches += i                     // hashes can collide: verify
        }
    }
    return Result(matches, hits)
}

// Every window's rolling hash, to compare with substring(...).hashCode().
fun rollingHashes(text: String, m: Int): IntArray {
    var high = 1
    repeat(m - 1) { high *= 31 }
    val out = IntArray(text.length - m + 1)
    var h = 0
    for (i in 0 until m) h = 31 * h + text[i].code
    for (i in out.indices) {
        if (i > 0) h = 31 * (h - text[i - 1].code * high) + text[i + m - 1].code
        out[i] = h
    }
    return out
}

fun main() {
    val book = File("data/alice.txt").readText()
    val rolled = rollingHashes(book, 8)
    val same = rolled.indices.all { rolled[it] == book.substring(it, it + 8).hashCode() }
    println("${rolled.size} windows of length 8: rolling hash == substring hashCode? $same")

    val r = search(book, "Alice")
    var naive = 0
    var from = book.indexOf("Alice")
    while (from >= 0) { naive++; from = book.indexOf("Alice", from + 1) }
    println("\"Alice\": ${r.matches.size} matches, ${r.hashHits} hash hits, indexOf finds $naive")

    val c = search("BBAaBB", "Aa")                       // "Aa" and "BB" share a hashCode: 2112
    println("\"Aa\" in \"BBAaBB\": matches at ${c.matches}, ${c.hashHits} hash hits")
}
