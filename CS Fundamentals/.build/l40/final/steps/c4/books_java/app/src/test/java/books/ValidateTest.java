package books;

import static org.junit.jupiter.api.Assertions.assertEquals;

import books.Model.NewBook;
import java.util.List;
import org.junit.jupiter.api.Test;

// Plain function, plain tests: no server, no HTTP, runs in milliseconds.
class ValidateTest {
    @Test void aValidBookHasNoErrors() {
        assertEquals(List.of(), Model.validate(new NewBook("Dune", 1965)));
    }

    @Test void missingFieldsAreBothReported() {
        assertEquals(List.of("title is required", "year is required"), Model.validate(new NewBook(null, null)));
    }

    @Test void aBlankTitleAndAFarFutureYearAreBothReported() {
        assertEquals(2, Model.validate(new NewBook("  ", 3000)).size());
    }
}
