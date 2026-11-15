import java.util.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Quick-find: slow but obviously right. id[p] is p's component name.
class NaiveSets(n: Int) {
    private val id = IntArray(n) { it }
    fun connected(p: Int, q: Int) = id[p] == id[q]
    fun union(p: Int, q: Int): Boolean {
        val a = id[p]; val b = id[q]
        if (a == b) return false
        for (i in id.indices) if (id[i] == a) id[i] = b
        return true
    }
    fun sizeOf(p: Int) = id.count { it == id[p] }
}

class DisjointSetsTest {
    @Test fun `every site starts alone`() {
        val s = DisjointSets(5)
        for (p in 0 until 5) {
            assertEquals(p, s.find(p))
            assertEquals(1, s.sizeOf(p))
        }
    }

    @Test fun `union reports whether it merged`() {
        val s = DisjointSets(3)
        assertTrue(s.union(0, 1))
        assertFalse("already connected", s.union(1, 0))
        assertEquals(2, s.sizeOf(1))
        assertEquals(1, s.sizeOf(2))
    }

    @Test fun `the smaller tree goes under the larger, in either argument order`() {
        for (smallFirst in listOf(true, false)) {
            val s = DisjointSets(3)
            s.union(0, 1)
            val root = s.find(0)
            if (smallFirst) s.union(2, 0) else s.union(0, 2)
            assertEquals("the root of the bigger tree {0, 1} must stay the root", root, s.find(2))
            assertEquals(3, s.sizeOf(2))
        }
    }

    @Test fun `find points every node on its path at the root`() {
        val s = DisjointSets(8)
        // Equal merges, as in UnionFind.kt: height 3 whichever way ties go.
        for ((p, q) in listOf(0 to 1, 2 to 3, 4 to 5, 6 to 7, 0 to 2, 4 to 6, 0 to 4)) s.union(p, q)
        fun pathOf(p: Int): List<Int> = if (s.parent[p] == p) listOf(p) else listOf(p) + pathOf(s.parent[p])
        val deepest = (0 until 8).maxBy { pathOf(it).size }
        val path = pathOf(deepest)
        assertEquals("the unions should build a tree of height 3", 4, path.size)
        val root = s.find(deepest)
        for (x in path) assertEquals("parent of $x after find($deepest)", root, s.parent[x])
    }

    @Test fun `agrees with a naive union-find on 500 random operations`() {
        val rnd = Random(49)
        val n = 12
        val fast = DisjointSets(n); val slow = NaiveSets(n)
        repeat(500) { t ->
            val p = rnd.nextInt(n); val q = rnd.nextInt(n)
            assertEquals("step $t: union($p, $q)", slow.union(p, q), fast.union(p, q))
            val a = rnd.nextInt(n); val b = rnd.nextInt(n)
            assertEquals("step $t: connected($a, $b)", slow.connected(a, b), fast.find(a) == fast.find(b))
            assertEquals("step $t: sizeOf($a)", slow.sizeOf(a), fast.sizeOf(a))
        }
    }
}
