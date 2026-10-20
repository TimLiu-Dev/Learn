fun main() {
    val words = listOf("kiwi", "fig", "banana", "plum", "cherry")

    println("== List (eager)")
    val fromList = words
        .filter { println("  filter $it"); it.length > 3 }
        .map { println("  map $it"); it.uppercase() }
        .take(2)
    println("result $fromList")

    println("== Sequence (lazy)")
    val fromSeq = words.asSequence()
        .filter { println("  filter $it"); it.length > 3 }
        .map { println("  map $it"); it.uppercase() }
        .take(2)
    println("pipeline built, nothing ran yet")
    println("result ${fromSeq.toList()}")
}
