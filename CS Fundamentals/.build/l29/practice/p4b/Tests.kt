import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

// Answers "home" and "work" on another thread, fails for "mars", never answers "nowhere".
class FakeGeocoder : Geocoder {
    val started = AtomicInteger()
    val cancelled = AtomicInteger()
    val listeners = CopyOnWriteArrayList<PlaceListener>()

    override fun lookup(address: String, onResult: (Double, Double) -> Unit, onError: (Exception) -> Unit): Lookup {
        started.incrementAndGet()
        when (address) {
            "home" -> thread { onResult(51.5, -0.1) }
            "work" -> thread { onResult(48.9, 2.3) }
            "mars" -> thread { onError(IllegalArgumentException("no such address: mars")) }
        }
        return Lookup { cancelled.incrementAndGet() }
    }

    override fun addListener(listener: PlaceListener) {
        listeners += listener
        thread { for (p in listOf("Oslo", "Lima", "Pune", "Rome")) listener.onPlace(p) }
    }

    override fun removeListener(listener: PlaceListener) { listeners -= listener }
}

class BridgeTest {
    @Test fun `find returns the coordinates from the callback`() = runBlocking {
        val geo = FakeGeocoder()
        assertEquals(51.5 to -0.1, geo.find("home"))
        assertEquals(48.9 to 2.3, geo.find("work"))
    }

    @Test fun `find throws the error the API reports`() = runBlocking {
        try {
            geo().find("mars")
            fail("expected an IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("no such address: mars", e.message)
        }
    }

    @Test fun `cancelling find cancels the lookup`() = runBlocking {
        val geo = FakeGeocoder()
        val job = launch { geo.find("nowhere") }
        while (geo.started.get() == 0) yield()            // wait until the lookup has started
        job.cancelAndJoin()
        assertEquals("lookups cancelled after the coroutine was cancelled", 1, geo.cancelled.get())
    }

    @Test fun `places delivers the places in order`() = runBlocking {
        assertEquals(listOf("Oslo", "Lima", "Pune"), geo().places().take(3).toList())
    }

    @Test fun `places removes its listener when collection stops`() = runBlocking {
        val geo = FakeGeocoder()
        geo.places().take(2).toList()
        assertEquals("listeners still registered", 0, geo.listeners.size)
    }

    private fun geo() = FakeGeocoder()
}
