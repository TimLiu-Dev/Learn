import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RightQuestionTest {
    @Test fun `equal score sheets match`() {
        assertTrue(sameScores(intArrayOf(90, 75), intArrayOf(90, 75)))
    }

    @Test fun `reordered or shorter score sheets don't match`() {
        assertFalse("same numbers, different order", sameScores(intArrayOf(90, 75), intArrayOf(75, 90)))
        assertFalse("one score missing", sameScores(intArrayOf(90), intArrayOf(90, 75)))
    }

    @Test fun `an alias is the same list`() {
        val a = mutableListOf(1, 2)
        val b = a
        assertTrue(isSameList(a, b))
    }

    @Test fun `an equal copy is not the same list`() {
        val a = mutableListOf(1, 2)
        val copy = a.toMutableList()
        assertFalse("adding to the copy would not change the original", isSameList(a, copy))
    }

    @Test fun `a typed name is found by its contents`() {
        val typed = StringBuilder("Lin").append("us").toString()   // built at runtime, like real input
        assertEquals(1, findName(listOf("Ada", "Linus", "Grace"), typed))
    }

    @Test fun `a missing name gives -1`() {
        assertEquals(-1, findName(listOf("Ada", "Grace"), "Alan"))
    }
}
