import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SubsetSumTest {
    private val sizes = intArrayOf(3, 34, 4, 12, 5, 2)

    @Test fun `a correct certificate is accepted`() {
        assertTrue("4 + 5 = 9", verify(sizes, 9, listOf(2, 4)))
    }

    @Test fun `a wrong sum is rejected`() {
        assertFalse("3 + 34 is not 9", verify(sizes, 9, listOf(0, 1)))
    }

    @Test fun `a certificate can't use the same item twice`() {
        assertFalse("there is only one 3", verify(sizes, 6, listOf(0, 0)))
    }

    @Test fun `an index outside the list is rejected, not thrown`() {
        assertFalse(verify(sizes, 9, listOf(6)))
        assertFalse(verify(sizes, 9, listOf(-1, 2, 4)))
    }

    @Test fun `search finds a subset that the verifier accepts`() {
        val pick = search(sizes, 9)
        assertNotNull("some subset sums to 9", pick)
        assertTrue(verify(sizes, 9, pick!!))
    }

    @Test fun `search returns null when no subset works, and the empty subset sums to 0`() {
        assertNull("even sizes can't make 7", search(intArrayOf(2, 4, 6, 8), 7))
        assertEquals(emptyList<Int>(), search(sizes, 0))
    }
}
