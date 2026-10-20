fun main() {
    val q = ArrayDeque<String>()                    // kotlin.collections.ArrayDeque
    q.addLast("A")                                  // enqueue at the back
    q.addLast("B")
    println("${q.first()} ${q.removeFirst()} ${q.removeFirst()}")
    println("${q.removeFirstOrNull()} ${q.firstOrNull()}")  // empty: the ...OrNull versions
    try { q.removeFirst() } catch (e: NoSuchElementException) { println("removeFirst(): $e") }

    val maybe = ArrayDeque<String?>()               // Kotlin's deque can hold nulls if you ask
    maybe.addLast(null)
    println("holds null: $maybe size=${maybe.size}")

    val d = ArrayDeque(listOf(2))
    d.addFirst(1); d.addLast(3)
    println("$d first=${d.first()} last=${d.last()}")
}
