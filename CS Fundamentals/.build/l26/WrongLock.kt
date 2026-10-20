import kotlin.concurrent.thread

var a = 0

fun main() {
    val workers = List(2) {
        thread {
            repeat(1_000_000) {
                synchronized(Any()) { a++ }      // a brand-new lock object every time
            }
        }
    }
    workers.forEach { it.join() }
    println("fresh lock each call: a = $a (expected 2000000)")
}
