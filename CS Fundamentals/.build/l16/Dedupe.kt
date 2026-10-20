// "Remove duplicates, keep first-seen order": the kind of code an assistant writes in seconds.
fun uniqueSlow(words: List<String>): List<String> {
    val result = mutableListOf<String>()
    for (w in words) {
        if (w !in result) result.add(w)        // `in` on a List scans it: O(n) per check
    }
    return result                              // n checks * O(n) each = O(n^2)
}

fun uniqueFast(words: List<String>): List<String> {
    val seen = HashSet<String>()
    val result = mutableListOf<String>()
    for (w in words) {
        if (seen.add(w)) result.add(w)         // HashSet add/contains: O(1) on average
    }
    return result                              // n * O(1) = O(n)
}

fun main() {
    val words = listOf("to", "be", "or", "not", "to", "be")
    println(uniqueSlow(words))
    println(uniqueFast(words))
    println(words.distinct())                  // the stdlib one: a LinkedHashSet inside, O(n)
}
