package shelf.store;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import shelf.domain.Book;

// In-memory stores: fast fakes for unit tests (L38). synchronized makes them safe for concurrent requests too.
public final class MemoryBookStore implements BookStore {
    private final TreeMap<Long, Book> books = new TreeMap<>();   // sorted by id, like ORDER BY id
    private long nextId = 1;

    private List<Book> matching(String author) {
        return books.values().stream().filter(b -> author == null || b.author().equals(author)).toList();
    }

    @Override
    public synchronized Book add(String title, String author, Integer year) {
        Book book = new Book(nextId++, title, author, year);
        books.put(book.id(), book);
        return book;
    }

    @Override
    public synchronized Optional<Book> find(long id) {
        return Optional.ofNullable(books.get(id));
    }

    @Override
    public synchronized List<Book> list(int limit, int offset, String author) {
        return matching(author).stream().skip(offset).limit(limit).toList();
    }

    @Override
    public synchronized int count(String author) {
        return matching(author).size();
    }

    @Override
    public synchronized Optional<Book> update(long id, String title, String author, Integer year) {
        if (!books.containsKey(id)) return Optional.empty();
        Book book = new Book(id, title, author, year);
        books.put(id, book);
        return Optional.of(book);
    }

    @Override
    public synchronized boolean delete(long id) {
        return books.remove(id) != null;
    }
}
