package shelf.service;

import java.util.List;
import java.util.Optional;
import shelf.domain.Book;
import shelf.store.BookStore;
import shelf.store.LoanStore;

public final class BookService {
    public record NewBook(String title, String author, Integer year) {}          // already validated
    public record Page<T>(List<T> items, int limit, int offset, int total) {}
    public enum DeleteResult { DELETED, NOT_FOUND, HAS_LOANS }

    private final BookStore books;
    private final LoanStore loans;

    public BookService(BookStore books, LoanStore loans) {
        this.books = books;
        this.loans = loans;
    }

    public Book create(NewBook book) {
        return books.add(book.title(), book.author(), book.year());
    }

    public Optional<Book> get(long id) {
        return books.find(id);
    }

    public Optional<Book> update(long id, NewBook book) {
        return books.update(id, book.title(), book.author(), book.year());
    }

    public Page<Book> list(int limit, int offset, String author) {
        return new Page<>(books.list(limit, offset, author), limit, offset, books.count(author));
    }

    // Loan history is worth keeping, so a book that was ever lent can't be deleted.
    public DeleteResult delete(long id) {
        if (books.find(id).isEmpty()) return DeleteResult.NOT_FOUND;
        if (loans.anyForBook(id)) return DeleteResult.HAS_LOANS;
        return books.delete(id) ? DeleteResult.DELETED : DeleteResult.NOT_FOUND;
    }
}
