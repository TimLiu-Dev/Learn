import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// The real grade(), and a maker for buggy copies of it ("mutants").
fun grade(score: Int, valid: IntRange = 0..100, a: Int = 90, b: Int = 80, c: Int = 70): String = when {
    score !in valid -> "invalid"
    score >= a -> "A"
    score >= b -> "B"
    score >= c -> "C"
    else -> "F"
}

// Caught: some correct row gets a different answer from the mutant.
fun caught(mutant: (Int) -> String): Boolean =
    gradeCases.any { (score, want) -> want == grade(score) && mutant(score) != want }

class GradeCasesTest {
    @Test fun `every row agrees with the spec`() {
        for ((score, want) in gradeCases) assertEquals("the row for $score", grade(score), want)
    }

    @Test fun `at most 10 rows`() {
        assertTrue("${gradeCases.size} rows: pick sharper ones", gradeCases.size <= 10)
    }

    @Test fun `catches an A that starts at 91, or at 89`() {
        assertTrue("no row catches an A that starts at 91", caught { grade(it, a = 91) })
        assertTrue("no row catches an A that starts at 89", caught { grade(it, a = 89) })
    }

    @Test fun `catches a B that starts at 81, and a C that starts at 71`() {
        assertTrue("no row catches a B that starts at 81", caught { grade(it, b = 81) })
        assertTrue("no row catches a C that starts at 71", caught { grade(it, c = 71) })
    }

    @Test fun `catches a validator that rejects 0 or 100`() {
        assertTrue("no row catches a validator that rejects 0", caught { grade(it, valid = 1..100) })
        assertTrue("no row catches a validator that rejects 100", caught { grade(it, valid = 0..99) })
    }

    @Test fun `catches a validator that accepts 101`() {
        assertTrue("no row catches a validator that accepts 101", caught { grade(it, valid = 0..101) })
    }
}
