import kotlin.concurrent.thread

fun main() {
    val a = thread { repeat(40) { print("A") } }
    val b = thread { repeat(40) { print("B") } }
    a.join(); b.join()
    println()
}
