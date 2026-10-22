package latefees

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

// Characterisation tests for calculateFees: green on the messy version, and they must stay green.
class LateFeesTest {
    private val today = LocalDate.of(2026, 10, 21)
    private fun d(month: Int, day: Int) = LocalDate.of(2026, month, day)
    private fun summary(vararg loans: Loan, premium: Boolean = false) = calculateFees(loans.toList(), today, premium, detailed = false)

    @Test fun `one late return`() =
        assertEquals("$1.25", summary(Loan("Dune", "standard", d(10, 1), d(10, 20))))

    @Test fun `a Sunday due date moves to Monday`() =
        assertEquals("$0.25", summary(Loan("Dune", "standard", d(9, 20), d(10, 6))))

    @Test fun `returned on the due date is free`() =
        assertEquals("$0.00", summary(Loan("Dune", "standard", d(10, 1), d(10, 15))))

    @Test fun `dvd still out pays its rate plus a 2 dollar surcharge`() =
        assertEquals("$8.00", summary(Loan("Arrival", "dvd", d(10, 1), null)))

    @Test fun `the cap is 15 dollars, whatever the comment says`() =
        assertEquals("$15.00", summary(Loan("Emma", "new", d(8, 1), null)))

    @Test fun `premium takes 1 dollar off a late loan`() =
        assertEquals("$0.25", summary(Loan("Dune", "standard", d(10, 1), d(10, 20)), premium = true))

    @Test fun `premium discount stops at zero`() =
        assertEquals("$0.00", summary(Loan("Dune", "standard", d(10, 1), d(10, 16)), premium = true))

    @Test fun `unknown kinds pay the standard rate`() =
        assertEquals("$1.25", summary(Loan("Wired", "magazine", d(10, 1), d(10, 20))))

    @Test fun `returned before it was loaned is free`() =
        assertEquals("$0.00", summary(Loan("Dune", "standard", d(10, 10), d(10, 1))))

    @Test fun `whole dollars keep two zeros`() =
        assertEquals("$2.00", summary(Loan("Arrival", "dvd", d(10, 1), d(10, 17))))

    @Test fun `detailed report lists every loan and the total`() = assertEquals(
        "Late fees as of 2026-10-21\nDune: $1.25\nArrival: $8.00\nEmma: no fee\nTOTAL: $9.25",
        calculateFees(
            listOf(
                Loan("Dune", "standard", d(10, 1), d(10, 20)),
                Loan("Arrival", "dvd", d(10, 1), null),
                Loan("Emma", "new", d(10, 1), d(10, 15)),
            ),
            today, premium = false, detailed = true,
        ),
    )
}
