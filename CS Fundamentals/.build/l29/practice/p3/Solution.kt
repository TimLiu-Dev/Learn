import kotlinx.coroutines.flow.*

fun interface SearchApi {
    suspend fun page(number: Int): List<String>   // an empty page means "no more"
}

fun allItems(api: SearchApi): Flow<String> = flow {
    var number = 1
    while (true) {
        val items = api.page(number++)
        if (items.isEmpty()) break
        items.forEach { emit(it) }   // emit suspends until the collector is ready for more
    }
}

suspend fun firstMatches(api: SearchApi, word: String, n: Int): List<String> =
    allItems(api)
        .filter { it.contains(word, ignoreCase = true) }
        .map { it.uppercase() }
        .take(n)                     // after n items, the flow above is cancelled: no more pages
        .toList()
