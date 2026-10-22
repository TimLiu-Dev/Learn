package latefees;

import static latefees.LateFees.calculateFees;
import static org.junit.jupiter.api.Assertions.assertEquals;

import latefees.LateFees.Loan;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

// Characterisation tests for calculateFees: green on the messy version, and they must stay green.
class LateFeesTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 21);
    private static LocalDate d(int month, int day) { return LocalDate.of(2026, month, day); }
    private static String summary(Loan loan) { return calculateFees(List.of(loan), TODAY, false, false); }
    private static String premiumSummary(Loan loan) { return calculateFees(List.of(loan), TODAY, true, false); }

    @Test void oneLateReturn() {
        assertEquals("$1.25", summary(new Loan("Dune", "standard", d(10, 1), d(10, 20))));
    }
    @Test void aSundayDueDateMovesToMonday() {
        assertEquals("$0.25", summary(new Loan("Dune", "standard", d(9, 20), d(10, 6))));
    }
    @Test void returnedOnTheDueDateIsFree() {
        assertEquals("$0.00", summary(new Loan("Dune", "standard", d(10, 1), d(10, 15))));
    }
    @Test void dvdStillOutPaysItsRatePlusA2DollarSurcharge() {
        assertEquals("$8.00", summary(new Loan("Arrival", "dvd", d(10, 1), null)));
    }
    @Test void theCapIs15DollarsWhateverTheCommentSays() {
        assertEquals("$15.00", summary(new Loan("Emma", "new", d(8, 1), null)));
    }
    @Test void premiumTakes1DollarOffALateLoan() {
        assertEquals("$0.25", premiumSummary(new Loan("Dune", "standard", d(10, 1), d(10, 20))));
    }
    @Test void premiumDiscountStopsAtZero() {
        assertEquals("$0.00", premiumSummary(new Loan("Dune", "standard", d(10, 1), d(10, 16))));
    }
    @Test void unknownKindsPayTheStandardRate() {
        assertEquals("$1.25", summary(new Loan("Wired", "magazine", d(10, 1), d(10, 20))));
    }
    @Test void returnedBeforeItWasLoanedIsFree() {
        assertEquals("$0.00", summary(new Loan("Dune", "standard", d(10, 10), d(10, 1))));
    }
    @Test void wholeDollarsKeepTwoZeros() {
        assertEquals("$2.00", summary(new Loan("Arrival", "dvd", d(10, 1), d(10, 17))));
    }
    @Test void detailedReportListsEveryLoanAndTheTotal() {
        assertEquals("Late fees as of 2026-10-21\nDune: $1.25\nArrival: $8.00\nEmma: no fee\nTOTAL: $9.25",
                calculateFees(List.of(
                        new Loan("Dune", "standard", d(10, 1), d(10, 20)),
                        new Loan("Arrival", "dvd", d(10, 1), null),
                        new Loan("Emma", "new", d(10, 1), d(10, 15))),
                        TODAY, false, true));
    }
}
