package shelf.store;

import java.util.List;
import java.util.Optional;
import shelf.domain.Book;

// What the services need from storage (L38: the policy owns the interfaces).
public interface BookStore {
    Book add(String title, String author, Integer year);
    Optional<Book> find(long id);
    List<Book> list(int limit, int offset, String author);     // ordered by id; author may be null
    int count(String author);
    Optional<Book> update(long id, String title, String author, Integer year);   // empty: no such book
    boolean delete(long id);
}
