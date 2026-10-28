package dev.shelf

import java.net.URI
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import shelf.service.BookService
import shelf.service.LendResult
import shelf.service.LendingService

data class BookJson(val id: Long, val title: String, val author: String, val year: Int?)
data class LoanRequest(val bookId: Long? = null, val memberId: Long? = null)
data class LoanJson(val id: Long, val bookId: Long, val memberId: Long, val loaned: String)

// NOT RUN HERE. Replaces Shelf's Router + two routes. BookService and LendingService are Shelf's, unchanged.
@RestController
class ShelfController(private val books: BookService, private val lending: LendingService) {

    @GetMapping("/books/{id}")
    fun book(@PathVariable id: Long): BookJson {
        val b = books.get(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No book with id $id")
        return BookJson(b.id, b.title, b.author, b.year)
    }

    @PostMapping("/loans")
    fun lend(@RequestBody body: LoanRequest): ResponseEntity<LoanJson> {
        val bookId = body.bookId ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "bookId is required")
        val memberId = body.memberId ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "memberId is required")
        return when (val r = lending.lend(bookId, memberId)) {
            is LendResult.Lent -> ResponseEntity.created(URI.create("/loans/${r.loan.id}"))
                .body(LoanJson(r.loan.id, r.loan.bookId, r.loan.memberId, r.loan.loaned.toString()))
            LendResult.UnknownBook -> throw ResponseStatusException(HttpStatus.NOT_FOUND, "No book with id $bookId")
            LendResult.UnknownMember -> throw ResponseStatusException(HttpStatus.NOT_FOUND, "No member with id $memberId")
            LendResult.BookOnLoan -> throw ResponseStatusException(HttpStatus.CONFLICT, "Book $bookId is already on loan")
            LendResult.LimitReached -> throw ResponseStatusException(HttpStatus.CONFLICT,
                "Member $memberId already has ${LendingService.MAX_OPEN_LOANS} open loans")
        }
    }
}
