fun main() {
    val words = listOf("pear", "fig", "kiwi", "apple", "date")
    val result = words.sorted().sortedBy { it.length }
    println(result.joinToString(" "))
}
