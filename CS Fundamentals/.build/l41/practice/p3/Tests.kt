import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WeightedLruTest {
    private fun filled(): WeightedLru = WeightedLru(10).apply {
        put("a", "aaaa")          // weight 4
        put("b", "bbb")           // weight 3
        put("c", "ccc")           // weight 3: total 10, exactly full
    }

    @Test fun `get moves an entry to the front`() {
        val cache = filled()
        assertEquals("aaaa", cache.get("a"))
        assertEquals("keys, most recent first", listOf("a", "c", "b"), cache.keys())
        assertNull("a miss", cache.get("zzz"))
    }

    @Test fun `a heavy entry evicts least recently used entries until it fits`() {
        val cache = filled()
        cache.put("d", "ddddd")   // weight 5: total 15, so a (11) and b (8) must go
        assertEquals("evicted, in order", listOf("a", "b"), cache.evicted)
        assertEquals("keys, most recent first", listOf("d", "c"), cache.keys())
        assertEquals("total weight", 8, cache.totalWeight)
    }

    @Test fun `a get saves an entry from eviction`() {
        val cache = filled()
        cache.get("a")
        cache.put("d", "ddddd")
        assertEquals("evicted, in order", listOf("b", "c"), cache.evicted)
        assertEquals("keys, most recent first", listOf("d", "a"), cache.keys())
    }

    @Test fun `updating a key changes its weight and never evicts the key itself`() {
        val cache = filled()
        cache.put("b", "bbbbbbbb") // weight 3 -> 8: total 15, so a (11) and c (8) go
        assertEquals("evicted, in order", listOf("a", "c"), cache.evicted)
        assertEquals("keys", listOf("b"), cache.keys())
        assertEquals("total weight", 8, cache.totalWeight)
        assertEquals("bbbbbbbb", cache.get("b"))
    }

    @Test fun `a value heavier than the whole cache is not stored and drops the stale copy`() {
        val cache = filled()
        cache.put("a", "x".repeat(11))
        assertNull("the old value of a must not be served", cache.get("a"))
        assertEquals("keys", listOf("c", "b"), cache.keys())
        assertEquals("total weight", 6, cache.totalWeight)
        assertEquals("nothing was evicted", emptyList<String>(), cache.evicted)
    }
}
