import java.util.*;

public class Decorators {
    record Book(String isbn, String title) {}

    interface BookCatalog {
        Optional<Book> find(String isbn);
        List<Book> all();
        int size();
    }

    static class SlowCatalog implements BookCatalog {           // pretend each lookup is a network call
        private final Map<String, Book> books = Map.of(
            "9780441172719", new Book("9780441172719", "Dune"),
            "9780141439587", new Book("9780141439587", "Emma"));
        public Optional<Book> find(String isbn) {
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            return Optional.ofNullable(books.get(isbn));
        }
        public List<Book> all() { return List.copyOf(books.values()); }
        public int size() { return books.size(); }
    }

    // Decorator: same interface, wraps another BookCatalog, adds one behaviour
    static class CachingCatalog implements BookCatalog {
        private final BookCatalog inner;
        private final Map<String, Optional<Book>> cache = new HashMap<>();
        int misses = 0;
        CachingCatalog(BookCatalog inner) { this.inner = inner; }
        public Optional<Book> find(String isbn) {
            return cache.computeIfAbsent(isbn, k -> { misses++; return inner.find(k); });
        }
        public List<Book> all() { return inner.all(); }          // forwarding boilerplate: one line per
        public int size() { return inner.size(); }               // method we DON'T want to change
    }

    static class LoggingCatalog implements BookCatalog {
        private final BookCatalog inner;
        LoggingCatalog(BookCatalog inner) { this.inner = inner; }
        public Optional<Book> find(String isbn) { System.out.println("  find(" + isbn + ")"); return inner.find(isbn); }
        public List<Book> all() { return inner.all(); }
        public int size() { return inner.size(); }
    }

    static long timeFiveLookups(BookCatalog c) {
        long start = System.nanoTime();
        for (String isbn : List.of("9780441172719", "9780141439587", "9780441172719", "9780441172719", "9780000000000"))
            c.find(isbn);
        return (System.nanoTime() - start) / 1_000_000;
    }

    public static void main(String[] args) {
        System.out.println("plain:  " + timeFiveLookups(new SlowCatalog()) + " ms");
        CachingCatalog cached = new CachingCatalog(new SlowCatalog());
        BookCatalog stack = new LoggingCatalog(cached);            // decorators stack like Russian dolls
        System.out.println("cached: " + timeFiveLookups(stack) + " ms, misses=" + cached.misses);
        System.out.println("size still forwards: " + stack.size());
    }
}
