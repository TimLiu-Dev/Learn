import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import tools.jackson.databind.json.JsonMapper;

public class Design {
    record Book(long id, String title, int year) {}
    record NewBook(String title, int year) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)        // one envelope for every list endpoint
    record Page<T>(List<T> items, int limit, int offset, int total, String next) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)        // RFC 9457 Problem Details; "errors" is an extension
    record Problem(String type, String title, int status, String detail, List<String> errors) {}

    static <T> Page<T> paginate(List<T> all, int limit, int offset, String path) {
        List<T> items = all.stream().skip(offset).limit(limit).toList();
        String next = offset + limit < all.size() ? path + "?limit=" + limit + "&offset=" + (offset + limit) : null;
        return new Page<>(items, limit, offset, all.size(), next);
    }

    static List<String> validate(NewBook b) {         // collect ALL problems, not just the first
        var errors = new ArrayList<String>();
        if (b.title() == null || b.title().isBlank()) errors.add("title must not be blank");
        if (b.year() < 1450 || b.year() > 2100) errors.add("year must be between 1450 and 2100");
        return errors;
    }

    public static void main(String[] args) {
        var mapper = JsonMapper.builder().build();
        List<Book> books = LongStream.rangeClosed(1, 5).mapToObj(i -> new Book(i, "Book " + i, 2000 + (int) i)).toList();
        System.out.println(mapper.writeValueAsString(paginate(books, 2, 0, "/books")));
        System.out.println(mapper.writeValueAsString(paginate(books, 2, 4, "/books")));

        List<String> errors = validate(new NewBook(" ", 3000));
        var problem = new Problem("https://example.com/problems/validation", "Invalid book", 400,
                                  errors.size() + " field(s) failed validation", errors);
        System.out.println(mapper.writeValueAsString(problem));
    }
}
