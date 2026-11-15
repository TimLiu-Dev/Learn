import kotlinx.coroutines.*

fun interface Db {
    fun countRows(table: String): Int
}

// Default is for CPU work: it has only as many threads as cores,
// so blocking calls fill it up and the rest have to wait.
suspend fun loadCounts(db: Db, tables: List<String>): Map<String, Int> =
    withContext(Dispatchers.Default) {
        tables.map { table -> async { table to db.countRows(table) } }
            .awaitAll()
            .toMap()
    }
