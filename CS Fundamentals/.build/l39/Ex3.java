import java.util.*;

public class Ex3 {
    interface BookCatalog { Optional<String> find(String isbn); int size(); }

    static class FakeCatalog implements BookCatalog {
        public Optional<String> find(String isbn) { return Optional.of("title-" + isbn); }
        public int size() { return 1; }
    }
    static class CountingCatalog implements BookCatalog {
        private final BookCatalog inner; int calls = 0;
        CountingCatalog(BookCatalog inner) { this.inner = inner; }
        public Optional<String> find(String isbn) { calls++; return inner.find(isbn); }
        public int size() { return inner.size(); }
    }
    static class CachingCatalog implements BookCatalog {
        private final BookCatalog inner; private final Map<String, Optional<String>> cache = new HashMap<>();
        CachingCatalog(BookCatalog inner) { this.inner = inner; }
        public Optional<String> find(String isbn) { return cache.computeIfAbsent(isbn, inner::find); }
        public int size() { return inner.size(); }
    }

    public static void main(String[] args) {
        List<String> lookups = List.of("a", "b", "a", "a", "b");
        CountingCatalog outside = new CountingCatalog(new CachingCatalog(new FakeCatalog()));   // counts every request
        lookups.forEach(outside::find);
        CountingCatalog inside = new CountingCatalog(new FakeCatalog());
        BookCatalog cachedInside = new CachingCatalog(inside);                                // counts only cache misses
        lookups.forEach(cachedInside::find);
        System.out.println("counter outside the cache: " + outside.calls);
        System.out.println("counter inside the cache:  " + inside.calls);
    }
}
