// BEFORE: what is l? what does f switch on? what is r?
fun calc(l: String, f: Boolean): Map<String, Int> {
    val r = mutableMapOf<String, Int>()
    for (w in l.split(Regex("[^\\p{L}]+"))) {
        if (w.isEmpty()) continue
        val k = if (f) w.lowercase() else w
        r[k] = (r[k] ?: 0) + 1
    }
    return r
}

// AFTER
/**
 * Counts how often each word appears in [text].
 *
 * A word is a run of letters; everything else separates words.
 * @param ignoreCase when true, "The" and "the" count as the same word.
 * @return word to count, in first-seen order.
 */
fun wordCounts(text: String, ignoreCase: Boolean = false): Map<String, Int> {
    val counts = mutableMapOf<String, Int>()
    for (word in text.split(Regex("[^\\p{L}]+"))) {
        if (word.isEmpty()) continue
        val key = if (ignoreCase) word.lowercase() else word
        counts[key] = (counts[key] ?: 0) + 1
    }
    return counts
}

fun main() {
    val text = "The cat sat on the mat. The mat was flat."
    println(calc(text, true))                         // true... what?
    println(wordCounts(text, ignoreCase = true))      // reads like a sentence
    println(calc(text, true) == wordCounts(text, ignoreCase = true))
}
