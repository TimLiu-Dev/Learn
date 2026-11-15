import kotlinx.coroutines.*

// A blocking call you can't change.
fun interface Db {
    fun countRows(table: String): Int
}

// Main-safe: the blocking calls move to IO, and the caller resumes on its own dispatcher.
suspend fun loadCounts(db: Db, tables: List<String>): Map<String, Int> =
    withContext(Dispatchers.IO) {
        tables.map { table -> async { table to db.countRows(table) } }
            .awaitAll()
            .toMap()
    }
