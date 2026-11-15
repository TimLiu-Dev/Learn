import java.io.IOException
import java.util.Collections
import kotlinx.coroutines.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

// name takes 100 ms, friendCount 200 ms, posts 300 ms. For id 13, friendCount fails.
class FakeApi : Api {
    val log: MutableList<String> = Collections.synchronizedList(mutableListOf())

    private suspend fun <T> call(what: String, id: Int, ms: Long, result: () -> T): T {
        log += "start $what $id"
        try {
            delay(ms)
        } catch (e: CancellationException) {
            log += "cancel $what $id"
            throw e
        }
        log += "end $what $id"
        return result()
    }

    override suspend fun name(id: Int) = call("name", id, 100) { "user$id" }
    override suspend fun friendCount(id: Int) = call("friends", id, 200) {
        if (id == 13) throw IOException("friends down") else id * 10
    }
    override suspend fun posts(id: Int) = call("posts", id, 300) { listOf("hello from $id") }
}

// Every "start" must come before the first "end": that's what "at the same time" means.
fun assertAllStartedBeforeAnyEnded(log: List<String>, starts: Int) {
    val firstEnd = log.indexOfFirst { it.startsWith("end") }
    val before = if (firstEnd < 0) log else log.take(firstEnd)
    assertEquals("calls started before the first one ended. Log: $log",
        starts, before.count { it.startsWith("start") })
}

class LoadPageTest {
    @Test fun `a page has all three parts`() = runBlocking {
        assertEquals(Page("user1", 10, listOf("hello from 1")), loadPage(FakeApi(), 1))
    }

    @Test fun `the three calls run at the same time`() = runBlocking {
        val api = FakeApi()
        loadPage(api, 1)
        assertAllStartedBeforeAnyEnded(api.log, 3)
    }

    @Test fun `a failing call fails the page and cancels the slower call`() = runBlocking {
        val api = FakeApi()
        try {
            loadPage(api, 13)
            fail("loadPage(13) should throw, because friendCount fails")
        } catch (e: IOException) {
            assertEquals("friends down", e.message)
        }
        assertTrue("posts should be cancelled when friends fails. Log: ${api.log}", "cancel posts 13" in api.log)
        assertFalse("posts must not finish. Log: ${api.log}", "end posts 13" in api.log)
    }

    @Test fun `one broken page does not stop the others`() = runBlocking {
        val api = FakeApi()
        val pages = loadPages(api, listOf(1, 13, 2))
        assertEquals(Page("user1", 10, listOf("hello from 1")), pages[0])
        assertNull("the broken page", pages[1])
        assertEquals(Page("user2", 20, listOf("hello from 2")), pages[2])
    }

    @Test fun `pages load at the same time`() = runBlocking {
        val api = FakeApi()
        loadPages(api, listOf(1, 2))
        assertAllStartedBeforeAnyEnded(api.log, 6)
    }
}
