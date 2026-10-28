package shelf.store;

/** Thrown by LoanStore.add when the book already has an open loan. */
public final class BookAlreadyOnLoan extends RuntimeException {
    public BookAlreadyOnLoan(long bookId) {
        super("book " + bookId + " is already on loan");
    }
}
