import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

data class User(val id: Int, val name: String)

fun bill(
    userId: Int,
    findUser: (Int) -> CompletableFuture<User>,
    ordersFor: (User) -> CompletableFuture<List<Int>>,
    discount: () -> CompletableFuture<Int>,
): CompletableFuture<String> {
    val off = discount()                                   // starts now, while the user is looked up
    return findUser(userId)
        .thenCompose { user ->                             // flatMap: ordersFor returns a future
            ordersFor(user).thenCombine(off) { amounts, d -> "${user.name} pays ${amounts.sum() - d}" }
        }
        .exceptionally { e ->                              // e arrives wrapped in CompletionException
            val cause = if (e is CompletionException) e.cause ?: e else e
            "failed: ${cause.message}"
        }
}
