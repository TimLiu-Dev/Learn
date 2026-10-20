import java.util.concurrent.CompletableFuture

fun lookupAsync(id: Int): CompletableFuture<String> = CompletableFuture.supplyAsync { "user-$id" }

fun main() {
    val id = CompletableFuture.completedFuture(42)

    // thenApply with a function that returns a future: you get a future of a future
    val nested: CompletableFuture<CompletableFuture<String>> = id.thenApply { lookupAsync(it) }
    // thenCompose flattens it, like flatMap on a list (L14)
    val flat: CompletableFuture<String> = id.thenCompose { lookupAsync(it) }

    println("nested.join()        = ${nested.join()}")
    println("nested.join().join() = ${nested.join().join()}")
    println("flat.join()          = ${flat.join()}")
}
