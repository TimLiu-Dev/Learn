import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

public class Adapter {
    record Book(String isbn, String title) {}

    // OUR interface: the rest of the app depends on this (L38: depend on abstractions)
    interface BookCatalog {
        Optional<Book> find(String isbn);
        List<Book> all();
    }

    // A legacy class we can't change: it only speaks XML
    static class XmlBookFeed {
        String fetchXml() {
            return "<books><book isbn=\"9780441172719\" title=\"Dune\"/>"
                 + "<book isbn=\"9780141439587\" title=\"Emma\"/></books>";
        }
    }

    // The adapter: implements our interface by translating to the legacy API
    static class XmlFeedCatalog implements BookCatalog {
        private final XmlBookFeed feed;
        XmlFeedCatalog(XmlBookFeed feed) { this.feed = feed; }

        public List<Book> all() {
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(feed.fetchXml())));
                NodeList nodes = doc.getElementsByTagName("book");
                List<Book> books = new ArrayList<>();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element e = (Element) nodes.item(i);
                    books.add(new Book(e.getAttribute("isbn"), e.getAttribute("title")));
                }
                return books;
            } catch (Exception e) {
                throw new IllegalStateException("bad feed", e);   // the legacy API's checked exceptions stop here
            }
        }
        public Optional<Book> find(String isbn) {
            return all().stream().filter(b -> b.isbn().equals(isbn)).findFirst();
        }
    }

    public static void main(String[] args) throws IOException {
        BookCatalog catalog = new XmlFeedCatalog(new XmlBookFeed());
        System.out.println(catalog.all());
        System.out.println(catalog.find("9780141439587").map(Book::title).orElse("not found"));

        // JDK adapter 1: Arrays.asList makes an array look like a List; writes go through to the array
        String[] shelf = {"Dune", "Emma"};
        List<String> view = Arrays.asList(shelf);
        view.set(0, "Ulysses");
        System.out.println("array after view.set: " + Arrays.toString(shelf));
        try { view.add("Beloved"); }
        catch (UnsupportedOperationException e) { System.out.println("view.add -> UnsupportedOperationException"); }

        // JDK adapter 2: InputStreamReader turns a byte stream into a character Reader (L22)
        InputStream bytes = new ByteArrayInputStream("café".getBytes(StandardCharsets.UTF_8));
        Reader chars = new InputStreamReader(bytes, StandardCharsets.UTF_8);
        int n = 0;
        while (chars.read() != -1) n++;
        System.out.println("5 bytes in, " + n + " chars out");
    }
}
