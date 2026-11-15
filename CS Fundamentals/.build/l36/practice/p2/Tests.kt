import org.junit.Assert.assertEquals
import org.junit.Test

class OptimisticTest {
    private val interleaved = listOf("T1 read", "T2 read", "T1 write", "T2 write")

    @Test fun `a blind write loses the first deposit`() {
        val acc = Account(10000)
        runSchedule(acc, mapOf("T1" to Deposit(1000), "T2" to Deposit(1000)), interleaved, optimistic = false)
        assertEquals("the lost update from Part 2", 11000, acc.balance)
    }

    @Test fun `a matching version updates the row and bumps the version`() {
        val acc = Account(10000, version = 7)
        assertEquals("update count", 1, acc.updateIfVersion(10500, 7))
        assertEquals(10500, acc.balance)
        assertEquals("version after a successful write", 8, acc.version)
    }

    @Test fun `a stale version changes nothing`() {
        val acc = Account(10000, version = 8)
        assertEquals("update count", 0, acc.updateIfVersion(10500, 7))
        assertEquals("balance must be untouched", 10000, acc.balance)
        assertEquals("version must be untouched", 8, acc.version)
    }

    @Test fun `optimistic writes keep both deposits`() {
        val acc = Account(10000)
        val retries = runSchedule(acc, mapOf("T1" to Deposit(1000), "T2" to Deposit(1000)), interleaved, optimistic = true)
        assertEquals("final balance", 12000, acc.balance)
        assertEquals("T2 should retry once", 1, retries)
    }

    @Test fun `a serial schedule needs no retries`() {
        val acc = Account(10000)
        val serial = listOf("T1 read", "T1 write", "T2 read", "T2 write")
        val retries = runSchedule(acc, mapOf("T1" to Deposit(1000), "T2" to Deposit(1000)), serial, optimistic = true)
        assertEquals("final balance", 12000, acc.balance)
        assertEquals("retries", 0, retries)
    }

    @Test fun `three readers, then three writers`() {
        val acc = Account(0)
        val txs = mapOf("T1" to Deposit(1), "T2" to Deposit(10), "T3" to Deposit(100))
        val steps = listOf("T1 read", "T2 read", "T3 read", "T1 write", "T2 write", "T3 write")
        val retries = runSchedule(acc, txs, steps, optimistic = true)
        assertEquals("every deposit counted once", 111, acc.balance)
        assertEquals("T2 and T3 both lose the first race", 2, retries)
    }
}
