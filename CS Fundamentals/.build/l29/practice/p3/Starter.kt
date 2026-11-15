import kotlinx.coroutines.flow.*

fun interface SearchApi {
    suspend fun page(number: Int): List<String>   // an empty page means "no more"
}

// Every item on every page, fetching a page only when the collector needs it.
fun allItems(api: SearchApi): Flow<String> = flow {
    TODO("fetch page 1, 2, … and emit each item, until a page is empty")
}

// The first n items containing word (ignoring case), uppercased.
suspend fun firstMatches(api: SearchApi, word: String, n: Int): List<String> =
    TODO("an operator pipeline on allItems(api), then toList()")
