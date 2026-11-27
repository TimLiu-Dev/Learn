package catalogue.api;

import catalogue.internal.DefaultCatalogue;
import java.util.List;
import platform.EventBus;

public interface Catalogue {
    void addBook(String isbn, String title, String author);
    void addCopy(String isbn, String barcode);
    void withdrawCopy(String barcode);
    List<BookSummary> search(String text);

    static Catalogue create(EventBus bus) { return new DefaultCatalogue(bus); }
}
