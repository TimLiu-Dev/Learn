import org.junit.Assert.assertEquals
import org.junit.Test

class ParallelPipelinesTest {
    private val nums = (1..200_000).map { it % 1000 }
    private val words = List(400_000) { if (it % 3 == 0) "parallel" else "seq" }

    @Test fun `squares keep the input order`() {
        assertEquals(listOf(9, 1, 4), squares(listOf(3, 1, 2)))
        val expected = nums.map { it * it }
        repeat(5) { run ->
            val got = try { squares(nums) } catch (e: Exception) { throw AssertionError("run $run threw $e") }
            assertEquals("run $run: size", expected.size, got.size)
            assertEquals("run $run: first index where the order differs", -1, expected.indices.firstOrNull { got[it] != expected[it] } ?: -1)
        }
    }

    @Test fun `balance adds the opening amount exactly once`() {
        assertEquals("no deposits", 100L, balance(100L, emptyList()))
        val deposits = (1L..100_000L).toList()
        repeat(5) { run -> assertEquals("run $run", 100L + 5_000_050_000L, balance(100L, deposits)) }
    }

    @Test fun `countLong counts every long word`() {
        assertEquals(1, countLong(listOf("thread", "lock", "pool")))
        repeat(5) { run -> assertEquals("run $run", 133_334, countLong(words)) }
    }
}
