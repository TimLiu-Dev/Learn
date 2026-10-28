// Excerpt of L45 Routes.kt, lines 26–28, 67–77 (tested there; not a standalone file)
    get("/books/{id}") { req ->
        ok(BookOut.from(books.get(req.id()) ?: throw ApiError.notFound(req.path)))
    }

    // ...

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
