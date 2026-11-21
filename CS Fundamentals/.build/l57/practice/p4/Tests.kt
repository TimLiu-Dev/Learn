import org.junit.Assert.assertEquals
import org.junit.Test

class CoordinatorTest {
    private fun coordinator() = Coordinator(setOf("orders-db", "stock-db"))

    @Test fun `every yes commits`() {
        val c = coordinator()
        assertEquals("one yes of two", Decision.WAITING, c.onVote("orders-db", true))
        assertEquals("both said yes", Decision.COMMIT, c.onVote("stock-db", true))
    }

    @Test fun `one no aborts at once`() {
        val c = coordinator()
        assertEquals("before orders-db has even voted", Decision.ABORT, c.onVote("stock-db", false))
    }

    @Test fun `a timeout with a vote missing aborts`() {
        val c = coordinator()
        c.onVote("orders-db", true)
        assertEquals(Decision.ABORT, c.onTimeout())
    }

    @Test fun `a duplicated yes counts once`() {
        val c = coordinator()
        c.onVote("orders-db", true)
        assertEquals("orders-db's vote delivered twice; stock-db hasn't voted", Decision.WAITING, c.onVote("orders-db", true))
    }

    @Test fun `a commit is final`() {
        val c = coordinator()
        c.onVote("orders-db", true)
        c.onVote("stock-db", true)
        assertEquals("a late timeout", Decision.COMMIT, c.onTimeout())
        assertEquals(Decision.COMMIT, c.decision)
    }

    @Test fun `an abort is final`() {
        val c = Coordinator(setOf("a", "b", "c"))
        c.onVote("a", true)
        c.onTimeout()
        c.onVote("b", true)
        assertEquals("late yes votes after the timeout", Decision.ABORT, c.onVote("c", true))
    }
}
