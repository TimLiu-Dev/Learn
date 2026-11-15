import kotlinx.coroutines.*

data class Page(val name: String, val friends: Int, val posts: List<String>)

interface Api {
    suspend fun name(id: Int): String
    suspend fun friendCount(id: Int): Int
    suspend fun posts(id: Int): List<String>
}

// coroutineScope: all-or-nothing. A failing child cancels its siblings,
// and the scope rethrows the failure once they have all stopped.
suspend fun loadPage(api: Api, id: Int): Page = coroutineScope {
    val name = async { api.name(id) }
    val friends = async { api.friendCount(id) }
    val posts = async { api.posts(id) }
    Page(name.await(), friends.await(), posts.await())
}

// supervisorScope: best effort. A failed page stays inside its own Deferred.
suspend fun loadPages(api: Api, ids: List<Int>): List<Page?> = supervisorScope {
    val pages = ids.map { id -> async { loadPage(api, id) } }
    pages.map { page ->
        try {
            page.await()
        } catch (e: CancellationException) {
            throw e                     // never swallow cancellation
        } catch (e: Exception) {
            null
        }
    }
}
