package shelf.http

import java.net.URLEncoder
import shelf.service.BookService
import shelf.service.DeleteResult
import shelf.service.LendResult
import shelf.service.LendingService
import shelf.service.RegisterResult
import shelf.service.ReturnResult

// Handlers translate HTTP <-> service calls. No SQL here, and no business rules.
fun Router.shelfRoutes(books: BookService, lending: LendingService) {
    get("/books") { req ->
        val limit = req.intQuery("limit", 10, 1..100)
        val offset = req.intQuery("offset", 0, 0..Int.MAX_VALUE)
        val author = req.query["author"]
        val page = books.list(limit, offset, author)
        val next = if (offset + limit >= page.total) null
                   else "/books?limit=$limit&offset=${offset + limit}" +
                        (author?.let { "&author=" + URLEncoder.encode(it, Charsets.UTF_8) } ?: "")
        ok(BookPage(page.items.map(BookOut::from), limit, offset, page.total, next))
    }

    get("/books/{id}") { req ->
        ok(BookOut.from(books.get(req.id()) ?: throw ApiError.notFound(req.path)))
    }

    post("/books") { req ->
        val book = books.create(req.bodyAs<BookIn>().validate())
        ok(BookOut.from(book), 201, mapOf("Location" to "/books/${book.id}"))
    }

    put("/books/{id}") { req ->
        val id = req.id()
        val book = books.update(id, req.bodyAs<BookIn>().validate()) ?: throw ApiError.notFound(req.path)
        ok(BookOut.from(book))
    }

    delete("/books/{id}") { req ->
        when (books.delete(req.id())) {
            DeleteResult.DELETED -> noContent()
            DeleteResult.NOT_FOUND -> throw ApiError.notFound(req.path)
            DeleteResult.HAS_LOANS -> throw ApiError.conflict("Book ${req.id()} has loans, so it can't be deleted")
        }
    }

    post("/members") { req ->
        val (name, email) = req.bodyAs<MemberIn>().validate()
        when (val r = lending.register(name, email)) {
            is RegisterResult.Registered ->
                ok(MemberOut.from(r.member), 201, mapOf("Location" to "/members/${r.member.id}"))
            RegisterResult.EmailTaken -> throw ApiError.conflict("$email is already registered")
        }
    }

    get("/members/{id}") { req ->
        ok(MemberOut.from(lending.member(req.id()) ?: throw ApiError.notFound(req.path)))
    }

    get("/members/{id}/loans") { req ->
        val loans = lending.loansOf(req.id()) ?: throw ApiError.notFound(req.path)
        ok(loans.map(LoanOut::from))
    }

    post("/loans") { req ->
        val (bookId, memberId) = req.bodyAs<LoanIn>().validate()
        when (val r = lending.lend(bookId, memberId)) {
            is LendResult.Lent -> ok(LoanOut.from(r.loan), 201, mapOf("Location" to "/loans/${r.loan.id}"))
            LendResult.UnknownBook -> throw ApiError(404, "Not Found", "No book with id $bookId")
            LendResult.UnknownMember -> throw ApiError(404, "Not Found", "No member with id $memberId")
            LendResult.BookOnLoan -> throw ApiError.conflict("Book $bookId is already on loan")
            LendResult.LimitReached ->
                throw ApiError.conflict("Member $memberId already has ${LendingService.MAX_OPEN_LOANS} open loans")
        }
    }

    get("/loans/{id}") { req ->
        ok(LoanOut.from(lending.loan(req.id()) ?: throw ApiError.notFound(req.path)))
    }

    post("/loans/{id}/return") { req ->
        when (val r = lending.returnLoan(req.id())) {
            is ReturnResult.Returned -> ok(LoanOut.from(r.loan))
            ReturnResult.UnknownLoan -> throw ApiError.notFound(req.path)
            ReturnResult.AlreadyReturned -> throw ApiError.conflict("Loan ${req.id()} was already returned")
        }
    }
}

// Operations endpoints: for load balancers (L42) and monitoring (L43), not for users.
fun Router.opsRoutes(dbUp: () -> Boolean, metricsText: () -> String) {
    get("/health") {
        val up = dbUp()
        ok(Health(if (up) "UP" else "DOWN", if (up) "UP" else "DOWN"), if (up) 200 else 503)
    }
    get("/metrics") { Response(200, metricsText(), "text/plain; version=0.0.4") }
}
