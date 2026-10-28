// Spring Framework 7.0, following "Using @Transactional". Not run here.
@Service
class LendingService {
    private final LoanStore loans;

    LendingService(LoanStore loans) { this.loans = loans; }

    @Transactional                        // the proxy opens a transaction around this call
    public Loan lend(long bookId, long memberId) { return loans.add(bookId, memberId, LocalDate.now()); }

    public void lendBoth(long a, long b, long memberId) {
        lend(a, memberId);                // self-invocation: no proxy in between, so no transaction
        lend(b, memberId);
    }
}
