import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

fun tree(vararg keys: Int): Node? = keys.fold(null as Node?) { root, k -> insert(root, k) }

class BalanceFloorTest {
    private val seven = tree(50, 30, 70, 20, 40, 60, 80)

    @Test fun `the seven-node tree is balanced`() {
        assertTrue(isBalanced(seven))
    }

    @Test fun `an empty tree and a single node are balanced`() {
        assertTrue("empty tree", isBalanced(null))
        assertTrue("single node", isBalanced(tree(42)))
    }

    @Test fun `sorted inserts are not balanced`() {
        assertFalse("1, 2, 3, 4 makes a stick", isBalanced(tree(1, 2, 3, 4)))
    }

    @Test fun `a leaning subtree is caught even when the root looks fine`() {
        // 50's two sides both have height 3, but 30 has a left stick and no right child.
        assertFalse(isBalanced(tree(50, 30, 20, 10, 70, 80, 90)))
    }

    @Test fun `floor finds the key itself or the closest smaller one`() {
        assertEquals("exact match", 60, floor(seven, 60))
        assertEquals("45 falls between 40 and 50", 40, floor(seven, 45))
        assertEquals("65 falls between 60 and 70", 60, floor(seven, 65))
        assertEquals("above the maximum", 80, floor(seven, 99))
    }

    @Test fun `floor below the minimum is null`() {
        assertNull("nothing is <= 10", floor(seven, 10))
        assertNull("empty tree", floor(null, 5))
    }
}
