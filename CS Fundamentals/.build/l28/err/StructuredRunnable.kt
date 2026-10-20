import java.util.concurrent.StructuredTaskScope

// BUG: a bare lambda picks fork(Runnable), so get() returns null
fun findUser(): String { Thread.sleep(200); return "Ada" }
fun fetchOrder(fail: Boolean): Int {
    Thread.sleep(100)
    if (fail) throw IllegalStateException("order service down")
    return 7
}

fun handle(fail: Boolean): String =
    StructuredTaskScope.open<Any?>().use { scope ->           // each fork runs in a new virtual thread
        val user = scope.fork<String> { findUser() }
        val order = scope.fork<Int> { fetchOrder(fail) }
        scope.join()                                         // waits for both; throws if either failed
        "${user.get()} has order #${order.get()}"
    }

fun main() {
    var start = System.nanoTime()
    println("${handle(false)} (${(System.nanoTime() - start) / 1_000_000} ms)")
    start = System.nanoTime()
    try {
        handle(true)
    } catch (e: StructuredTaskScope.FailedException) {
        println("failed after ${(System.nanoTime() - start) / 1_000_000} ms: ${e.cause}")
    }
}
