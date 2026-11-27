import kotlin.coroutines.resume
import kotlinx.coroutines.*

fun main() = runBlocking {
    val r = suspendCancellableCoroutine<String> { cont ->
        cont.resume("first")
        try {
            cont.resume("second")
        } catch (e: IllegalStateException) {
            println("second resume threw")
        }
    }
    println(r)
}
