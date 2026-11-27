package lending.domain;

import java.time.LocalDate;

public record Loan(CopyId copy, LocalDate loaned, LocalDate returned) {
    public LocalDate due() { return loaned.plusDays(Borrower.LOAN_DAYS); }
    public boolean isOpen() { return returned == null; }
    public Loan withReturned(LocalDate day) { return new Loan(copy, loaned, day); }
}
