val text = """
    The cat sat on the mat. The mat was flat,
    and the cat was fat! A cat, a mat, a hat.
""".trimIndent()

fun main() {
    val counts: Map<String, Int> = Regex("\\p{L}+")      // a run of letters
        .findAll(text.lowercase())                        // Sequence<MatchResult>, lazy
        .map { it.value }
        .groupingBy { it }
        .eachCount()

    println("total words: ${counts.values.sum()}")
    println("unique words: ${counts.size}")

    counts.entries
        .maxWith(compareBy<Map.Entry<String, Int>> { it.value }.thenByDescending { it.key })
        .let { println("most frequent: ${it.key} (${it.value})") }

    val repeated = counts.filterValues { it >= 2 }
        .toSortedMap()
        .entries.joinToString(" ") { "${it.key}=${it.value}" }
    println("repeated: $repeated")
}
