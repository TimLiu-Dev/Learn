fun anagramGroups(words: List<String>): Collection<List<String>> {
    val groups = mutableMapOf<String, MutableList<String>>()     // LinkedHashMap: first-seen order
    for (w in words) {
        val key = w.toCharArray().sorted().joinToString("")      // "listen" -> "eilnst"
        groups.getOrPut(key) { mutableListOf() }.add(w)
    }
    return groups.values
}

fun firstUnique(s: String): Char? {
    val counts = mutableMapOf<Char, Int>()
    for (c in s) counts[c] = (counts[c] ?: 0) + 1
    return counts.entries.firstOrNull { it.value == 1 }?.key     // insertion order matters here
}

fun main() {
    println(anagramGroups(listOf("listen", "google", "enlist", "banana", "inlets", "gogole", "silent")))
    println("${firstUnique("swiss")} ${firstUnique("level")} ${firstUnique("aabb")}")
}
