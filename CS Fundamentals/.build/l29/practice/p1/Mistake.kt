import kotlinx.coroutines.*

data class Page(val name: String, val friends: Int, val posts: List<String>)

interface Api {
    suspend fun name(id: Int): String
    suspend fun friendCount(id: Int): Int
    suspend fun posts(id: Int): List<String>
}

// Looks fine, but each call waits for the one before it.
suspend fun loadPage(api: Api, id: Int): Page =
    Page(api.name(id), api.friendCount(id), api.posts(id))

suspend fun loadPages(api: Api, ids: List<Int>): List<Page?> = supervisorScope {
    val pages = ids.map { id -> async { loadPage(api, id) } }
    pages.map { page ->
        try {
            page.await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }
}
