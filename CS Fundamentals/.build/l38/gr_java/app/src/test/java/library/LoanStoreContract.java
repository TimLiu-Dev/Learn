package library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// The fake and the real store must behave the same. If the fake drifts, these tests catch it.
abstract class LoanStoreContract {
    abstract LoanStore newStore() throws Exception;
    void cleanUp() throws Exception {}

    LoanStore store;
    private final LocalDate oct1 = LocalDate.parse("2026-10-01");

    @BeforeEach void setUp() throws Exception { store = newStore(); }
    @AfterEach void tearDown() throws Exception { cleanUp(); }

    @Test void anAddedLoanCanBeFoundAndIsOpen() {
        Loan loan = store.add(7, 1, oct1);
        assertEquals(loan, store.find(loan.id()));
        assertEquals(loan, store.openForBook(7));
        assertEquals(List.of(loan), store.openForMember(1));
    }

    @Test void closingALoanRecordsTheDateAndFreesTheBook() {
        Loan loan = store.add(7, 1, oct1);
        assertTrue(store.close(loan.id(), LocalDate.parse("2026-10-09")));
        assertEquals(LocalDate.parse("2026-10-09"), store.find(loan.id()).returned());
        assertNull(store.openForBook(7));
        assertFalse(store.close(loan.id(), LocalDate.parse("2026-10-10")));
    }

    @Test void aReturnedLoanNoLongerCountsForItsMember() {
        Loan first = store.add(7, 1, oct1);
        store.add(8, 1, oct1);
        store.close(first.id(), LocalDate.parse("2026-10-09"));
        assertEquals(List.of(8L), store.openForMember(1).stream().map(Loan::bookId).toList());
    }

    @Test void aBookCannotHaveTwoOpenLoans() {
        store.add(7, 1, oct1);
        assertThrows(RuntimeException.class, () -> store.add(7, 2, oct1));
    }

    @Test void unknownIdsGiveNullAndEmpty() {
        assertNull(store.find(42));
        assertNull(store.openForBook(42));
        assertEquals(List.of(), store.openForMember(42));
    }
}
