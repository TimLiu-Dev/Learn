// Make the comparator throw, then read the stack trace to see who was calling it.
val spy = Comparator<String> { a, b ->
    if (a == "kiwi" || b == "kiwi") throw IllegalStateException("who is sorting?")
    a.compareTo(b)
}

fun main() {
    try {
        listOf("pear", "fig", "kiwi", "apple").sortedWith(spy)
    } catch (e: IllegalStateException) {
        e.stackTrace.drop(1).take(5).forEach { println("  at $it") }
    }
}
