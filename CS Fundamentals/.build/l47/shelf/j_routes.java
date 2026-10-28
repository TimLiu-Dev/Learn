// Excerpt of L45 Routes.java, lines 39–40, 76–86 (tested there; not a standalone file)
        r.get("/books/{id}", req ->
            Json.ok(BookOut.from(books.get(req.id()).orElseThrow(() -> ApiError.notFound(req.path())))));

    // ...

        r.post("/loans", req -> {
            LoanIn in = Json.read(req, LoanIn.class).validated();
            return switch (lending.lend(in.bookId(), in.memberId())) {
                case LendResult.Lent(var loan) -> Json.ok(LoanOut.from(loan), 201, Map.of("Location", "/loans/" + loan.id()));
                case LendResult.UnknownBook() -> throw new ApiError(404, "Not Found", "No book with id " + in.bookId());
                case LendResult.UnknownMember() -> throw new ApiError(404, "Not Found", "No member with id " + in.memberId());
                case LendResult.BookOnLoan() -> throw ApiError.conflict("Book " + in.bookId() + " is already on loan");
                case LendResult.LimitReached() -> throw ApiError.conflict(
                    "Member " + in.memberId() + " already has " + LendingService.MAX_OPEN_LOANS + " open loans");
            };
        });
