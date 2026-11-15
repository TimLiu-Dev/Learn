import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

data class User(val id: Int, val name: String)

fun bill(
    userId: Int,
    findUser: (Int) -> CompletableFuture<User>,
    ordersFor: (User) -> CompletableFuture<List<Int>>,
    discount: () -> CompletableFuture<Int>,
): CompletableFuture<String> {
    val off = discount()
    return findUser(userId)
        .thenCompose { user ->
            ordersFor(user).thenCombine(off) { amounts, d -> "${user.name} pays ${amounts.sum() - d}" }
        }
        .exceptionally { e -> "failed: ${e.message}" }     // the wrapper's message, not the cause's
}
