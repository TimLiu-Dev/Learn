fun main() {
    val text = """
        The cat sat on the mat. The mat was flat,
        and the cat was fat! A cat, a mat, a hat.
    """.trimIndent()

    val counts = mutableMapOf<String, Int>()
    var total = 0
    for (word in text.lowercase().split(Regex("[^a-z]+"))) {
        if (word.isEmpty()) continue
        counts[word] = (counts[word] ?: 0) + 1
        total++
    }

    var best = ""
    var bestCount = 0
    for ((word, count) in counts) {
        if (count > bestCount || (count == bestCount && word < best)) {
            best = word
            bestCount = count
        }
    }

    println("total words: $total")
    println("unique words: ${counts.size}")
    println("most frequent: $best ($bestCount)")
    print("repeated:")
    for ((word, count) in counts.toSortedMap()) {
        if (count >= 2) print(" $word=$count")
    }
    println()
}
