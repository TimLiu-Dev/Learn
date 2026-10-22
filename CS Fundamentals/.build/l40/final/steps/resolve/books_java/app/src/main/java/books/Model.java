package books;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

public final class Model {
    private Model() {}

    public record Book(long id, String title, int year) {}

    public record NewBook(String title, Integer year) {}     // what clients send: no id, fields may be missing

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Page(List<Book> items, int limit, int offset, int total, String next) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)              // RFC 9457 Problem Details (+ "errors" extension)
    public record Problem(String type, String title, int status, String detail, List<String> errors) {}

    public static List<String> validate(NewBook b) {
        var errors = new ArrayList<String>();
        if (b.title() == null) errors.add("title is required");
        else if (b.title().isBlank()) errors.add("title must not be blank");
        if (b.year() == null) errors.add("year is required");
        else if (b.year() < 1440 || b.year() > 2100) errors.add("year must be between 1440 and 2100");
        return errors;
    }
}
