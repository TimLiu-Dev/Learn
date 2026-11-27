package catalogue.internal;

import java.util.LinkedHashSet;
import java.util.Set;

// The catalogue's Book: a title with bibliographic data and the copies it owns.
final class Book {
    final Isbn isbn;
    final String title;
    final String author;
    final Set<String> barcodes = new LinkedHashSet<>();

    Book(Isbn isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }
}
