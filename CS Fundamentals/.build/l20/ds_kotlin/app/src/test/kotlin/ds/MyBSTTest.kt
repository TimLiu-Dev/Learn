package ds

import kotlin.test.*

class MyBSTTest {
    private fun treeOf(vararg keys: Int) = MyBST<Int, String>().apply { for (k in keys) put(k, "v$k") }

    @Test fun `empty tree has nothing in it`() {
        val t = MyBST<Int, String>()
        assertEquals(0, t.size)
        assertNull(t[1])
        assertFalse(1 in t)
        assertEquals(0, t.height())
        assertNull(t.min())
        assertEquals(emptyList(), t.inOrderKeys())
    }

    @Test fun `put then get`() {
        val t = treeOf(50, 30, 70)
        assertEquals("v30", t[30])
        assertTrue(70 in t)
        assertNull(t[40])
        assertEquals(3, t.size)
    }

    @Test fun `put with an existing key replaces the value`() {
        val t = treeOf(50, 30)
        t.put(30, "new")
        assertEquals("new", t[30])
        assertEquals(2, t.size)
    }

    @Test fun `in-order keys come out sorted`() {
        assertEquals(listOf(20, 30, 40, 50, 60, 70, 80), treeOf(50, 30, 70, 20, 40, 60, 80).inOrderKeys())
    }

    @Test fun `min is the leftmost key`() {
        assertEquals(20, treeOf(50, 30, 70, 20, 40).min())
    }

    @Test fun `height of a balanced insert order`() {
        assertEquals(3, treeOf(4, 2, 6, 1, 3, 5, 7).height())
    }

    @Test fun `sorted insert order degenerates into a list`() {
        assertEquals(10, treeOf(*(1..10).toList().toIntArray()).height())
    }

    @Test fun `delete a leaf`() {
        val t = treeOf(50, 30, 70, 20)
        t.delete(20)
        assertEquals(listOf(30, 50, 70), t.inOrderKeys())
        assertEquals(3, t.size)
    }

    @Test fun `delete a node with one child`() {
        val t = treeOf(50, 30, 70, 60)
        t.delete(70)
        assertEquals(listOf(30, 50, 60), t.inOrderKeys())
        assertEquals("v60", t[60])
    }

    @Test fun `delete a node with two children keeps its value pairs`() {
        val t = treeOf(50, 30, 70, 20, 40, 60, 80, 65)
        t.delete(50)                                     // the root, two children
        assertEquals(listOf(20, 30, 40, 60, 65, 70, 80), t.inOrderKeys())
        assertEquals("v60", t[60])
        assertEquals("v65", t[65])
        assertNull(t[50])
        assertEquals(7, t.size)
    }

    @Test fun `deleting a missing key changes nothing`() {
        val t = treeOf(50, 30)
        t.delete(99)
        assertEquals(2, t.size)
    }

    @Test fun `works with any Comparable key`() {
        val t = MyBST<String, Int>()
        for (w in listOf("pear", "apple", "fig")) t.put(w, w.length)
        assertEquals(listOf("apple", "fig", "pear"), t.inOrderKeys())
        assertEquals(5, t["apple"])
    }
}
