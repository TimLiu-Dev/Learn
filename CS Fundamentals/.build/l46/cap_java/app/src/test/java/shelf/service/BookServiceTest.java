package shelf.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import shelf.domain.Book;
import shelf.service.BookService.DeleteResult;
import shelf.service.BookService.NewBook;
import shelf.store.MemoryBookStore;
import shelf.store.MemoryLoanStore;

class BookServiceTest {
    private final MemoryLoanStore loans = new MemoryLoanStore();
    private final BookService service = new BookService(new MemoryBookStore(), loans);

    @Test
    void aPageReportsTheTotalNotJustItsOwnSize() {
        for (int i = 0; i < 5; i++) service.create(new NewBook("Book " + i, i % 2 == 0 ? "Le Guin" : "Herbert", null));
        var page = service.list(2, 0, "Le Guin");
        assertEquals(List.of("Book 0", "Book 2"), page.items().stream().map(Book::title).toList());
        assertEquals(3, page.total());
    }

    @Test
    void aBookThatWasEverLentCannotBeDeleted() {
        Book lent = service.create(new NewBook("Dune", "Herbert", 1965));
        Book fresh = service.create(new NewBook("Emma", "Austen", 1815));
        loans.add(lent.id(), 1, LocalDate.of(2026, 1, 1));
        assertEquals(DeleteResult.HAS_LOANS, service.delete(lent.id()));
        assertEquals(DeleteResult.DELETED, service.delete(fresh.id()));
        assertEquals(DeleteResult.NOT_FOUND, service.delete(fresh.id()));
    }
}
