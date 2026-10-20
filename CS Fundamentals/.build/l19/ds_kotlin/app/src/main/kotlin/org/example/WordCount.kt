package org.example

/** L3's word-frequency report, counted with MyHashMap instead of the JDK's map. */
fun wordReport(text: String): String {
    val counts = MyHashMap<String, Int>()
    var total = 0
    for (raw in text.split(Regex("[^\\p{L}]+"))) {
        if (raw.isEmpty()) continue
        val word = raw.lowercase()
        counts[word] = (counts[word] ?: 0) + 1
        total++
    }

    val words = counts.keys().sorted()
    var best = words.first()
    for (w in words) if (counts[w]!! > counts[best]!!) best = w   // sorted, so ties keep the first
    val repeated = words.filter { counts[it]!! >= 2 }.joinToString(" ") { "$it=${counts[it]}" }

    return """
        |total words: $total
        |unique words: ${counts.size}
        |most frequent: $best (${counts[best]})
        |repeated: $repeated
    """.trimMargin()
}
