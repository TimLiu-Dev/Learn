class ArrayStack<T> {
    private val items = ArrayList<T>()              // the top is the END of the array

    fun push(item: T) { items.add(item) }           // amortised O(1), like L16's add
    fun pop(): T {                                  // O(1): nothing shifts
        if (items.isEmpty()) throw NoSuchElementException("stack is empty")
        return items.removeAt(items.size - 1)
    }
    fun peek(): T? = items.lastOrNull()             // O(1)
    fun isEmpty() = items.isEmpty()
    val size get() = items.size
}

fun main() {
    val undo = ArrayStack<String>()
    undo.push("type 'Hi'")
    undo.push("bold")
    undo.push("delete word")
    println("peek: ${undo.peek()}  size: ${undo.size}")
    while (!undo.isEmpty()) println("undo ${undo.pop()}")   // last in, first out
    try { undo.pop() } catch (e: NoSuchElementException) { println("pop: ${e.message}") }
}
