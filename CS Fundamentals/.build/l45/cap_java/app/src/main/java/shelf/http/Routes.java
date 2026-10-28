package shelf.http;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import shelf.domain.Book;
import shelf.http.Dtos.BookIn;
import shelf.http.Dtos.BookOut;
import shelf.http.Dtos.BookPage;
import shelf.http.Dtos.Health;
import shelf.http.Dtos.LoanIn;
import shelf.http.Dtos.LoanOut;
import shelf.http.Dtos.MemberIn;
import shelf.http.Dtos.MemberOut;
import shelf.service.BookService;
import shelf.service.LendingService;
import shelf.service.LendingService.LendResult;
import shelf.service.LendingService.RegisterResult;
import shelf.service.LendingService.ReturnResult;

// Handlers translate HTTP <-> service calls. No SQL here, and no business rules.
public final class Routes {
    private Routes() {}

    public static void register(Router r, BookService books, LendingService lending) {
        r.get("/health", req -> Json.ok(new Health("UP")));

        r.get("/books", req -> {
            int limit = req.intQuery("limit", 10, 1, 100);
            int offset = req.intQuery("offset", 0, 0, Integer.MAX_VALUE);
            String author = req.query("author");
            BookService.Page<Book> page = books.list(limit, offset, author);
            String next = offset + limit >= page.total() ? null
                : "/books?limit=" + limit + "&offset=" + (offset + limit)
                  + (author == null ? "" : "&author=" + URLEncoder.encode(author, StandardCharsets.UTF_8));
            return Json.ok(new BookPage(page.items().stream().map(BookOut::from).toList(), limit, offset, page.total(), next));
        });

        r.get("/books/{id}", req ->
            Json.ok(BookOut.from(books.get(req.id()).orElseThrow(() -> ApiError.notFound(req.path())))));

        r.post("/books", req -> {
            Book book = books.create(Json.read(req, BookIn.class).validate());
            return Json.ok(BookOut.from(book), 201, Map.of("Location", "/books/" + book.id()));
        });

        r.put("/books/{id}", req -> {
            long id = req.id();
            Book book = books.update(id, Json.read(req, BookIn.class).validate())
                .orElseThrow(() -> ApiError.notFound(req.path()));
            return Json.ok(BookOut.from(book));
        });

        r.delete("/books/{id}", req -> switch (books.delete(req.id())) {
            case DELETED -> Json.noContent();
            case NOT_FOUND -> throw ApiError.notFound(req.path());
            case HAS_LOANS -> throw ApiError.conflict("Book " + req.id() + " has loans, so it can't be deleted");
        });

        r.post("/members", req -> {
            MemberIn in = Json.read(req, MemberIn.class).validated();
            return switch (lending.register(in.name(), in.email())) {
                case RegisterResult.Registered(var member) ->
                    Json.ok(MemberOut.from(member), 201, Map.of("Location", "/members/" + member.id()));
                case RegisterResult.EmailTaken() -> throw ApiError.conflict(in.email() + " is already registered");
            };
        });

        r.get("/members/{id}", req ->
            Json.ok(MemberOut.from(lending.member(req.id()).orElseThrow(() -> ApiError.notFound(req.path())))));

        r.get("/members/{id}/loans", req ->
            Json.ok(lending.loansOf(req.id()).orElseThrow(() -> ApiError.notFound(req.path()))
                .stream().map(LoanOut::from).toList()));

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

        r.get("/loans/{id}", req ->
            Json.ok(LoanOut.from(lending.loan(req.id()).orElseThrow(() -> ApiError.notFound(req.path())))));

        r.post("/loans/{id}/return", req -> switch (lending.returnLoan(req.id())) {
            case ReturnResult.Returned(var loan) -> Json.ok(LoanOut.from(loan));
            case ReturnResult.UnknownLoan() -> throw ApiError.notFound(req.path());
            case ReturnResult.AlreadyReturned() -> throw ApiError.conflict("Loan " + req.id() + " was already returned");
        });
    }
}
