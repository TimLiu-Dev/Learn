package shelf.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import shelf.domain.Book;
import shelf.domain.Loan;
import shelf.domain.Member;
import shelf.service.BookService.NewBook;

// DTOs: the JSON shapes of the API. Validation happens here, at the boundary;
// a missing field arrives as null and becomes a clear message.
public final class Dtos {
    private static final Pattern EMAIL = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    private Dtos() {}

    public record BookIn(String title, String author, Integer year) {
        public NewBook validate() {
            List<String> errors = new ArrayList<>();
            if (title == null || title.isBlank()) errors.add("title is required");
            else if (title.length() > 200) errors.add("title must be at most 200 characters");
            if (author == null || author.isBlank()) errors.add("author is required");
            if (year != null && (year < 1400 || year > 2100)) errors.add("year must be between 1400 and 2100");
            if (!errors.isEmpty()) throw ApiError.invalid(errors);
            return new NewBook(title.strip(), author.strip(), year);
        }
    }

    public record BookOut(long id, String title, String author, Integer year) {
        public static BookOut from(Book b) {
            return new BookOut(b.id(), b.title(), b.author(), b.year());
        }
    }

    public record BookPage(List<BookOut> items, int limit, int offset, int total, String next) {}

    public record MemberIn(String name, String email) {
        public MemberIn validated() {
            List<String> errors = new ArrayList<>();
            if (name == null || name.isBlank()) errors.add("name is required");
            if (email == null || !EMAIL.matcher(email).matches()) errors.add("email must look like name@example.com");
            if (!errors.isEmpty()) throw ApiError.invalid(errors);
            return new MemberIn(name.strip(), email.toLowerCase(Locale.ROOT));
        }
    }

    public record MemberOut(long id, String name, String email) {
        public static MemberOut from(Member m) {
            return new MemberOut(m.id(), m.name(), m.email());
        }
    }

    public record LoanIn(Long bookId, Long memberId) {
        public LoanIn validated() {
            List<String> errors = new ArrayList<>();
            if (bookId == null) errors.add("bookId is required");
            if (memberId == null) errors.add("memberId is required");
            if (!errors.isEmpty()) throw ApiError.invalid(errors);
            return this;
        }
    }

    // Dates leave the API as ISO-8601 strings (L32).
    public record LoanOut(long id, long bookId, long memberId, String loaned, String returned) {
        public static LoanOut from(Loan l) {
            return new LoanOut(l.id(), l.bookId(), l.memberId(), l.loaned().toString(),
                               l.returned() == null ? null : l.returned().toString());
        }
    }

    public record Health(String status, String db) {}
}
