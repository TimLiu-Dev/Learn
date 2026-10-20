package demo

fun main() {
    val c = UnsafeCounter()
    repeat(3) { c.increment() }
    println("one thread, three increments: ${c.value}")
}
