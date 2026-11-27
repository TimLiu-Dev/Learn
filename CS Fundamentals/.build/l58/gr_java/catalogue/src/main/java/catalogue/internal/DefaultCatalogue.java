package catalogue.internal;

import catalogue.api.BookSummary;
import catalogue.api.Catalogue;
import catalogue.api.CatalogueEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import platform.EventBus;

// Public only because Catalogue.create, in another package, must construct it.
public final class DefaultCatalogue implements Catalogue {
    private final EventBus bus;
    private final Map<Isbn, Book> books = new LinkedHashMap<>();

    public DefaultCatalogue(EventBus bus) { this.bus = bus; }

    @Override
    public void addBook(String isbn, String title, String author) {
        Isbn key = Isbn.parse(isbn);
        if (books.containsKey(key)) throw new IllegalArgumentException("already catalogued: " + isbn);
        books.put(key, new Book(key, title, author));
    }

    @Override
    public void addCopy(String isbn, String barcode) {
        Book book = books.get(Isbn.parse(isbn));
        if (book == null) throw new IllegalArgumentException("unknown ISBN: " + isbn);
        if (books.values().stream().anyMatch(b -> b.barcodes.contains(barcode))) {
            throw new IllegalArgumentException("barcode in use: " + barcode);
        }
        book.barcodes.add(barcode);
        bus.publish(new CatalogueEvent.CopyAdded(barcode, book.isbn.digits(), book.title));
    }

    @Override
    public void withdrawCopy(String barcode) {
        Book book = books.values().stream().filter(b -> b.barcodes.contains(barcode)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown copy: " + barcode));
        book.barcodes.remove(barcode);
        bus.publish(new CatalogueEvent.CopyWithdrawn(barcode));
    }

    @Override
    public List<BookSummary> search(String text) {
        String t = text.toLowerCase(Locale.ROOT);
        return books.values().stream()
                .filter(b -> b.title.toLowerCase(Locale.ROOT).contains(t) || b.author.toLowerCase(Locale.ROOT).contains(t))
                .map(b -> new BookSummary(b.isbn.digits(), b.title, b.author, b.barcodes.size()))
                .toList();
    }
}
