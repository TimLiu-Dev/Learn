package library;

import java.time.LocalDate;

public record Loan(long id, long bookId, long memberId, LocalDate loaned, LocalDate returned) {
    public Loan withReturned(LocalDate day) { return new Loan(id, bookId, memberId, loaned, day); }
}
