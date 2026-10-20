class Stack<T> {
    private val items = mutableListOf<T>()

    fun push(item: T) { items.add(item) }

    fun pop(): T {
        check(items.isNotEmpty()) { "pop() on an empty stack" }
        return items.removeAt(items.size - 1)
    }

    fun peek(): T? = items.lastOrNull()          // null when empty

    fun isEmpty(): Boolean = items.isEmpty()

    // producer: Iterable is declared Iterable<out T>, so Iterable<Int> already fits Iterable<Number>
    fun pushAll(src: Iterable<T>) { for (item in src) push(item) }

    // consumer: any collection that accepts T or a supertype
    fun popAllInto(dst: MutableCollection<in T>) { while (!isEmpty()) dst.add(pop()) }

    override fun toString() = items.toString()
}

fun <T : Comparable<T>> maxOf(items: List<T>): T? {
    if (items.isEmpty()) return null
    var best = items[0]
    for (item in items) if (item > best) best = item
    return best
}

fun main() {
    val numbers = Stack<Number>()
    val ints: List<Int> = listOf(1, 2, 3)
    numbers.pushAll(ints)                        // List<Int> into Stack<Number>
    println("pushed ints: $numbers")
    println("peek: ${numbers.peek()}")
    numbers.push(2.5)                            // a Double is a Number too
    println("pop: ${numbers.pop()}")

    val log: MutableList<Any> = mutableListOf("log:")
    numbers.popAllInto(log)                      // Stack<Number> into MutableList<Any>
    println("drained: $log, empty now: ${numbers.isEmpty()}")
    println("peek on empty: ${numbers.peek()}")
    try {
        numbers.pop()
    } catch (e: IllegalStateException) {
        println("pop on empty: ${e.message}")
    }

    println("max: ${maxOf(listOf(4, 9, 2))} ${maxOf(listOf("pear", "fig"))} ${maxOf(emptyList<Int>())}")
}
