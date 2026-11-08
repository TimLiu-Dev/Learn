fun firstRepeat(words: List<String>): String? {
    val seen = mutableSetOf<String>()
    for (word in words) {
        seen.add(word)
        if (word in seen) return word
    }
    return null
}

fun lowStock(stock: Map<String, Int>, threshold: Int): List<String> {
    val low = mutableListOf<String>()
    for ((item, count) in stock) {
        if (count < threshold) low.add(item)
    }
    return low.sorted()
}
