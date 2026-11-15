import kotlinx.coroutines.flow.*

fun interface SearchApi {
    suspend fun page(number: Int): List<String>
}

// Cold, but eager inside: it fetches every page before emitting anything.
fun allItems(api: SearchApi): Flow<String> = flow {
    val all = mutableListOf<String>()
    var number = 1
    while (true) {
        val items = api.page(number++)
        if (items.isEmpty()) break
        all += items
    }
    all.forEach { emit(it) }
}

suspend fun firstMatches(api: SearchApi, word: String, n: Int): List<String> =
    allItems(api)
        .filter { it.contains(word, ignoreCase = true) }
        .map { it.uppercase() }
        .take(n)
        .toList()
