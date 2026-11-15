import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

data class User(val id: Int, val name: String)

fun bill(
    userId: Int,
    findUser: (Int) -> CompletableFuture<User>,
    ordersFor: (User) -> CompletableFuture<List<Int>>,
    discount: () -> CompletableFuture<Int>,
): CompletableFuture<String> {
    TODO("chain the three calls and handle failures, without waiting for any of them")
}
