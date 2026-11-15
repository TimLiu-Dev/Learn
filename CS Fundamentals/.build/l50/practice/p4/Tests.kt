import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

// Part 4's buggy window (`if` where it needs `while`) and the slow version it disagrees with.
fun shortestSlow(a: List<Int>, target: Int): Int {
    var best = 0
    for (i in a.indices) {
        var sum = 0
        for (j in i until a.size) {
            sum += a[j]
            if (sum >= target) { if (best == 0 || j - i + 1 < best) best = j - i + 1; break }
        }
    }
    return best
}

fun shortestBuggy(a: List<Int>, target: Int): Int {
    var left = 0; var sum = 0; var best = Int.MAX_VALUE
    for (right in a.indices) {
        sum += a[right]
        if (sum >= target) { best = minOf(best, right - left + 1); sum -= a[left++] }
    }
    return if (best == Int.MAX_VALUE) 0 else best
}

class ShrinkTest {
    @Test fun `an input with nothing to delete comes back unchanged`() {
        assertEquals(listOf(7), shrink(listOf(7)) { 7 in it })
    }

    @Test fun `everything the failure doesn't need is deleted`() {
        assertEquals(listOf(2, 5), shrink((1..6).toList()) { 2 in it && 5 in it })
    }

    @Test fun `the result keeps the original order`() {
        assertEquals(listOf("c", "b"), shrink(listOf("c", "a", "b", "a")) { it.firstOrNull() == "c" && "b" in it })
    }

    @Test fun `an input that passes is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { shrink(listOf(1, 2)) { false } }
    }

    @Test fun `the window bug shrinks to a tiny case`() {
        val fails = { a: List<Int> -> shortestSlow(a, 5) != shortestBuggy(a, 5) }
        val small = shrink(listOf(2, 1, 1, 1, 3, 1, 5, 2), fails)
        assertTrue("$small should still fail", fails(small))
        assertTrue("$small should have at most 3 numbers", small.size <= 3)
        for (i in small.indices) {
            assertFalse("$small without index $i still fails, so it isn't fully shrunk", fails(small.filterIndexed { j, _ -> j != i }))
        }
    }
}
