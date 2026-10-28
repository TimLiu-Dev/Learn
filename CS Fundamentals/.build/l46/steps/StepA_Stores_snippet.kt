/** Thrown by LoanStore.add when the book already has an open loan. */
class BookAlreadyOnLoan(bookId: Long) : RuntimeException("book $bookId is already on loan")
