package books;

import books.Model.Book;
import java.util.List;
import java.util.Optional;

/** Where books live. BookApi talks to this interface, never to a map (L38: depend on abstractions). */
public interface BookRepository {
    Book add(String title, int year);      // assigns the next id
    Optional<Book> find(long id);
    List<Book> all();                      // sorted by id
    boolean replace(Book book);            // false if no book has that id
    boolean delete(long id);               // false if no book has that id
}
