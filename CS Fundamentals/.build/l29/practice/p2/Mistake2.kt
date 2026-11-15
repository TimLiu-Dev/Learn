import kotlinx.coroutines.*

fun interface Db {
    fun countRows(table: String): Int
}

// async alone doesn't move anything: the children run on the caller's thread and block it.
suspend fun loadCounts(db: Db, tables: List<String>): Map<String, Int> = coroutineScope {
    tables.map { table -> async { table to db.countRows(table) } }
        .awaitAll()
        .toMap()
}
