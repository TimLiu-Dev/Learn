import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Step counts to experiment on. None of them times anything.
fun linearSteps(n: Int): Long = n.toLong()

fun nLogNSteps(n: Int): Long {                // a halving loop inside a linear one
    var steps = 0L
    repeat(n) {
        var k = n
        while (k > 1) { k /= 2; steps++ }
    }
    return steps
}

fun uniqueSlowComparisons(n: Int): Long {     // uniqueSlow from Part 1, counting comparisons
    val words = List(n) { "w$it" }            // all different: the worst case
    val result = mutableListOf<String>()
    var comparisons = 0L
    for (w in words) {
        comparisons += result.size            // `w !in result` compares w with every element so far
        result.add(w)
    }
    return comparisons
}

class DoublingTest {
    @Test fun `a linear count gives ratios of 2`() {
        assertEquals(listOf(2.0, 2.0, 2.0), doublingRatios(::linearSteps, 1_000, 4))
    }

    @Test fun `one size gives no ratios`() {
        assertEquals(emptyList<Double>(), doublingRatios(::linearSteps, 1_000, 1))
    }

    @Test fun `exponent rounds to the nearest power of two`() {
        assertEquals("ratio 1.0: constant", 0, exponent(1.0))
        assertEquals(1, exponent(2.1))
        assertEquals("ratio 3.9 is noisy quadratic", 2, exponent(3.9))
        assertEquals(3, exponent(7.6))
    }

    @Test fun `n log n is a bit more than 2, and still rounds to 1`() {
        val ratios = doublingRatios(::nLogNSteps, 1_024, 4)
        assertEquals(3, ratios.size)
        assertTrue("ratios $ratios should all be between 2 and 2.5", ratios.all { it > 2.0 && it < 2.5 })
        assertEquals(1, exponent(ratios.last()))
    }

    @Test fun `the hidden loop in uniqueSlow is quadratic`() {
        val ratios = doublingRatios(::uniqueSlowComparisons, 500, 5)
        assertEquals("ratios were $ratios", 2, exponent(ratios.last()))
    }
}
