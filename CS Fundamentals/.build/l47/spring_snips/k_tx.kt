// Spring Framework 7.0, following "Using @Transactional". Not run here.
@Service
class LendingService(private val loans: LoanStore) {

    @Transactional                        // the proxy opens a transaction around this call
    fun lend(bookId: Long, memberId: Long): Loan = loans.add(bookId, memberId, LocalDate.now())

    fun lendBoth(a: Long, b: Long, memberId: Long) {
        lend(a, memberId)                 // self-invocation: no proxy in between, so no transaction
        lend(b, memberId)
    }
}
