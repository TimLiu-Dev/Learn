import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

// The thread's name without the " @coroutine#N" that debug mode adds.
fun threadName(): String = Thread.currentThread().name.substringBefore(" @")

// Like a batch server: no table is answered until every table has been asked for.
// A call that waits more than half a second gives up and returns -1.
class FakeDb(tables: Int) : Db {
    private val asked = CountDownLatch(tables)
    val threads: MutableList<String> = Collections.synchronizedList(mutableListOf())

    override fun countRows(table: String): Int {
        threads += threadName()
        asked.countDown()
        val everyoneAsked = asked.await(500, TimeUnit.MILLISECONDS)   // blocks this thread
        return if (everyoneAsked) table.length * 100 else -1
    }

    // The UI's part: a click handled on the UI thread also opens the latch.
    fun click() = asked.countDown()
}

val ui = Executors.newSingleThreadExecutor { r -> Thread(r, "ui").apply { isDaemon = true } }.asCoroutineDispatcher()

// One more table than Dispatchers.Default has threads (it has max(2, cores)).
val tables = (1..maxOf(2, Runtime.getRuntime().availableProcessors()) + 1).map { "t$it" }

class MainSafeTest {
    @Test fun `every table gets its count`() = runBlocking {
        val counts = loadCounts(FakeDb(tables.size), tables)
        assertEquals("a -1 means that call waited, so the calls were not all running at once. Counts: $counts",
            tables.associateWith { 200 }, counts)
    }

    @Test fun `no blocking call runs on the UI thread`() = runBlocking(ui) {
        val db = FakeDb(tables.size)
        loadCounts(db, tables)
        assertFalse("countRows ran on: ${db.threads}", "ui" in db.threads)
    }

    @Test fun `the caller carries on on the UI thread`() = runBlocking(ui) {
        loadCounts(FakeDb(tables.size), tables)
        assertEquals("ui", threadName())
    }

    @Test fun `the UI still responds while the counts load`() = runBlocking(ui) {
        val db = FakeDb(tables.size + 1)   // this time it also waits for one click
        launch { db.click() }               // a UI coroutine: it only runs if the UI thread is free
        val counts = loadCounts(db, tables)
        assertEquals("a -1 means the click never ran: the UI thread was blocked. Counts: $counts",
            tables.associateWith { 200 }, counts)
    }

    @Test fun `no tables, no calls`() = runBlocking {
        assertEquals(emptyMap<String, Int>(), loadCounts(FakeDb(0), emptyList()))
    }
}
