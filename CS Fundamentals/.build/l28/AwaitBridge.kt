import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val cf = CompletableFuture.supplyAsync { Thread.sleep(100); "hello from a CompletableFuture" }
    val text = cf.await()          // suspends this coroutine instead of blocking a thread (L29)
    println("awaited: $text")
}
