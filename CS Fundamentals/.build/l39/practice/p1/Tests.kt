import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Test

// Two Recorders with the same name are EQUAL, but each keeps its own log.
data class Recorder(val name: String) : (Change) -> Unit {
    val got = mutableListOf<Change>()
    override fun invoke(c: Change) { got += c }
}

class PriceFeedTest {
    @Test fun `every subscriber hears every change`() {
        val feed = PriceFeed(100)
        val a = Recorder("a")
        val b = Recorder("b")
        feed.subscribe(a)
        feed.subscribe(b)
        feed.update(105)
        feed.update(99)
        assertEquals(listOf(Change(100, 105), Change(105, 99)), a.got)
        assertEquals(listOf(Change(100, 105), Change(105, 99)), b.got)
    }

    @Test fun `the default rule stays quiet when the price does not move`() {
        val feed = PriceFeed(100)
        val a = Recorder("a")
        feed.subscribe(a)
        feed.update(100)
        feed.update(101)
        feed.update(101)
        assertEquals(listOf(Change(100, 101)), a.got)
    }

    @Test fun `a rule the feed has never seen`() {
        val bigMoves: AlertRule = { from, to -> abs(to - from) >= 10 }
        val feed = PriceFeed(100, bigMoves)
        val a = Recorder("a")
        feed.subscribe(a)
        feed.update(105)      // +5: quiet, but the price is now 105
        feed.update(120)      // +15
        feed.update(112)      // -8: quiet
        assertEquals(listOf(Change(105, 120)), a.got)
    }

    @Test fun `closing a handle stops that listener, not an equal one`() {
        val feed = PriceFeed(100)
        val first = Recorder("screen")
        val second = Recorder("screen")
        feed.subscribe(first)
        val handle = feed.subscribe(second)
        handle.close()
        feed.update(101)
        assertEquals("first is still subscribed", listOf(Change(100, 101)), first.got)
        assertEquals("second was closed", emptyList<Change>(), second.got)
    }

    @Test fun `the same listener twice is called twice, until one handle closes`() {
        val feed = PriceFeed(100)
        val r = Recorder("r")
        val h1 = feed.subscribe(r)
        feed.subscribe(r)
        feed.update(101)
        h1.close()
        feed.update(102)
        assertEquals("prices heard", listOf(101, 101, 102), r.got.map { it.to })
    }

    @Test fun `closing a handle twice does nothing the second time`() {
        val feed = PriceFeed(100)
        val r = Recorder("r")
        val h1 = feed.subscribe(r)
        feed.subscribe(r)
        h1.close()
        h1.close()
        feed.update(101)
        assertEquals("one subscription is still open", listOf(101), r.got.map { it.to })
    }
}
