import kotlinx.coroutines.*

// A blocking call you can't change.
fun interface Db {
    fun countRows(table: String): Int
}

// Called on the UI thread. It must never block that thread.
suspend fun loadCounts(db: Db, tables: List<String>): Map<String, Int> =
    TODO("run every countRows call concurrently, off the caller's thread")
