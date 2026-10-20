package org.example

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class LruCacheTest {
    @Test fun `a missing key gives null`() {
        assertNull(LruCache<String, Int>(2)["x"])
    }

    @Test fun `evicts the least recently used entry`() {
        val c = LruCache<String, Int>(2)
        c["a"] = 1
        c["b"] = 2
        c["c"] = 3
        assertNull(c["a"])
        assertEquals(listOf("b", "c"), c.keys())
    }

    @Test fun `get counts as a use`() {
        val c = LruCache<String, Int>(2)
        c["a"] = 1
        c["b"] = 2
        c["a"]
        c["c"] = 3
        assertEquals(listOf("a", "c"), c.keys())
    }

    @Test fun `capacity must be positive`() {
        val e = assertFailsWith<IllegalArgumentException> { LruCache<String, Int>(0) }
        assertEquals("capacity must be positive: 0", e.message)
    }
}
