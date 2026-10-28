package dev.shelf;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import shelf.domain.Book;
import shelf.service.BookService;
import shelf.service.LendingService;
import shelf.service.LendingService.LendResult;

// NOT RUN HERE. Replaces Shelf's Router + two routes. BookService and LendingService are Shelf's, unchanged.
@RestController
public class ShelfController {
    public record BookJson(long id, String title, String author, Integer year) {}
    public record LoanRequest(Long bookId, Long memberId) {}
    public record LoanJson(long id, long bookId, long memberId, String loaned) {}

    private final BookService books;
    private final LendingService lending;

    public ShelfController(BookService books, LendingService lending) {
        this.books = books;
        this.lending = lending;
    }

    @GetMapping("/books/{id}")
    public BookJson book(@PathVariable long id) {
        Book b = books.get(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No book with id " + id));
        return new BookJson(b.id(), b.title(), b.author(), b.year());
    }

    @PostMapping("/loans")
    public ResponseEntity<LoanJson> lend(@RequestBody LoanRequest body) {
        if (body.bookId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bookId is required");
        if (body.memberId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "memberId is required");
        long bookId = body.bookId(), memberId = body.memberId();
        return switch (lending.lend(bookId, memberId)) {
            case LendResult.Lent(var loan) -> ResponseEntity.created(URI.create("/loans/" + loan.id()))
                .body(new LoanJson(loan.id(), loan.bookId(), loan.memberId(), loan.loaned().toString()));
            case LendResult.UnknownBook() -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No book with id " + bookId);
            case LendResult.UnknownMember() -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No member with id " + memberId);
            case LendResult.BookOnLoan() -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Book " + bookId + " is already on loan");
            case LendResult.LimitReached() -> throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Member " + memberId + " already has " + LendingService.MAX_OPEN_LOANS + " open loans");
        };
    }
}
