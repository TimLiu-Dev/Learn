import kotlinx.coroutines.*

data class Page(val name: String, val friends: Int, val posts: List<String>)

interface Api {
    suspend fun name(id: Int): String
    suspend fun friendCount(id: Int): Int
    suspend fun posts(id: Int): List<String>
}

// All three calls at once. One failure fails the page and cancels the rest.
suspend fun loadPage(api: Api, id: Int): Page =
    TODO("start the three calls concurrently, then build the Page")

// All pages at once, in the order of ids. A failed page becomes null.
suspend fun loadPages(api: Api, ids: List<Int>): List<Page?> =
    TODO("one child per page, where a failure stays local")
