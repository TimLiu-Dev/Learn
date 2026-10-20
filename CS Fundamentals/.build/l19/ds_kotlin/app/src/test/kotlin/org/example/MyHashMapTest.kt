package org.example

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MyHashMapTest {
    @Test fun `a new map is empty`() {
        val m = MyHashMap<String, Int>()
        assertEquals(0, m.size)
        assertNull(m["anything"])
    }

    @Test fun `get returns what put stored`() {
        val m = MyHashMap<String, Int>()
        m["cat"] = 3
        assertEquals(3, m["cat"])
        assertEquals(1, m.size)
    }

    @Test fun `put on an existing key replaces the value and returns the old one`() {
        val m = MyHashMap<String, Int>()
        assertNull(m.put("cat", 1))
        assertEquals(1, m.put("cat", 2))
        assertEquals(2, m["cat"])
        assertEquals(1, m.size)
    }

    @Test fun `containsKey tells present from absent`() {
        val m = MyHashMap<String, Int>()
        m["cat"] = 3
        assertTrue(m.containsKey("cat"))
        assertFalse(m.containsKey("dog"))
    }

    @Test fun `colliding keys are both kept`() {
        assertEquals("Aa".hashCode(), "BB".hashCode())     // a real collision
        val m = MyHashMap<String, Int>()
        m["Aa"] = 1
        m["BB"] = 2
        assertEquals(1, m["Aa"])
        assertEquals(2, m["BB"])
    }

    @Test fun `remove returns the value and forgets the key`() {
        val m = MyHashMap<String, Int>()
        m["cat"] = 3
        assertEquals(3, m.remove("cat"))
        assertNull(m["cat"])
        assertEquals(0, m.size)
        assertNull(m.remove("cat"))
    }

    @Test fun `remove from the middle of a chain keeps the neighbours`() {
        val m = MyHashMap<String, Int>()
        for ((i, k) in listOf("AaAa", "AaBB", "BBAa").withIndex()) m[k] = i   // all share one hash
        assertEquals(1, m.remove("AaBB"))
        assertEquals(0, m["AaAa"])
        assertEquals(2, m["BBAa"])
        assertEquals(2, m.size)
    }

    @Test fun `grows past the load factor and keeps every entry`() {
        val m = MyHashMap<Int, String>()
        val start = m.capacity
        for (i in 0 until 1000) m[i] = "v$i"
        assertEquals(1000, m.size)
        assertTrue(m.capacity > start)
        assertTrue(m.size <= m.capacity * 0.75)
        for (i in 0 until 1000) assertEquals("v$i", m[i])
    }

    @Test fun `keys lists every key once`() {
        val m = MyHashMap<String, Int>()
        for (k in listOf("a", "b", "c", "a")) m[k] = 0
        assertEquals(setOf("a", "b", "c"), m.keys().toSet())
        assertEquals(3, m.keys().size)
    }
}
