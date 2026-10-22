package books;

import books.Model.Book;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryBookRepository implements BookRepository {
    private final Map<Long, Book> books = new ConcurrentHashMap<>();   // L27: safe for concurrent handlers
    private final AtomicLong nextId = new AtomicLong(1);

    @Override public Book add(String title, int year) {
        Book book = new Book(nextId.getAndIncrement(), title, year);
        books.put(book.id(), book);
        return book;
    }

    @Override public Optional<Book> find(long id) { return Optional.ofNullable(books.get(id)); }

    @Override public List<Book> all() {
        return books.values().stream().sorted(Comparator.comparingLong(Book::id)).toList();
    }

    @Override public boolean replace(Book book) { return books.replace(book.id(), book) != null; }

    @Override public boolean delete(long id) { return books.remove(id) != null; }
}
