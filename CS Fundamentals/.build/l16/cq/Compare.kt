var comparisons = 0

class Word(val text: String) {
    override fun equals(other: Any?): Boolean {
        comparisons++
        return other is Word && other.text == text
    }
    override fun hashCode() = text.hashCode()
}

fun main() {
    val words = listOf("to", "be", "or", "not").map(::Word)
    val result = mutableListOf<Word>()
    for (w in words) if (w !in result) result.add(w)
    println(comparisons)
}
