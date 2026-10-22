import java.util.HashMap;
import java.util.List;
import java.util.Map;

// L32's books handler, shrunk to its essentials: no HTTP server, no JSON library.
// A "request" is (method, path, body); the body is "title,year".
public class Srp {
    // ---------- Before: one class, four reasons to change ----------
    static class BooksHandlerBefore {
        private final Map<Long, String> titles = new HashMap<>();
        private final Map<Long, Integer> years = new HashMap<>();
        private long nextId = 1;

        String handle(String method, String path, String body) {
            if (method.equals("POST") && path.equals("/books")) {
                String[] parts = body.split(",");                            // 1. the wire format
                String title = parts.length > 0 ? parts[0].trim() : "";
                Integer year = null;
                try { year = Integer.parseInt(parts[1].trim()); } catch (RuntimeException e) { }
                if (title.isBlank()) return "400 title must not be blank";   // 2. the business rules
                if (year == null || year < 1450 || year > 2100) return "400 year must be 1450..2100";
                long id = nextId++;                                          // 3. storage
                titles.put(id, title);
                years.put(id, year);
                return "201 {\"id\":" + id + ",\"title\":\"" + title + "\",\"year\":" + year + "}"; // 4. output
            }
            if (method.equals("GET") && path.startsWith("/books/")) {
                try {
                    long id = Long.parseLong(path.substring("/books/".length()));
                    if (!titles.containsKey(id)) return "404";
                    return "200 {\"id\":" + id + ",\"title\":\"" + titles.get(id) + "\",\"year\":" + years.get(id) + "}";
                } catch (NumberFormatException e) { return "404"; }
            }
            return "404";
        }
    }

    // ---------- After: one reason to change each ----------
    record Book(long id, String title, int year) {}
    record NewBook(String title, Integer year) {}

    static final class BookParser {                    // changes when the wire format changes
        static NewBook parse(String body) {
            String[] parts = body.split(",");
            Integer year = null;
            try { year = Integer.parseInt(parts[1].trim()); } catch (RuntimeException e) { }
            return new NewBook(parts.length > 0 ? parts[0].trim() : "", year);
        }
    }

    static final class BookRules {                     // changes when the librarians change a rule
        static String problem(NewBook b) {
            if (b.title().isBlank()) return "title must not be blank";
            if (b.year() == null || b.year() < 1450 || b.year() > 2100) return "year must be 1450..2100";
            return null;
        }
    }

    static final class BookStore {                     // changes when storage moves (L36: SQLite)
        private final Map<Long, Book> books = new HashMap<>();
        private long nextId = 1;
        Book add(String title, int year) {
            Book b = new Book(nextId++, title, year);
            books.put(b.id(), b);
            return b;
        }
        Book find(long id) { return books.get(id); }
    }

    static final class BookJson {                      // changes when the API's JSON changes
        static String write(Book b) {
            return "{\"id\":" + b.id() + ",\"title\":\"" + b.title() + "\",\"year\":" + b.year() + "}";
        }
    }

    static final class BooksHandler {                  // changes when routes change: nothing else
        private final BookStore store;
        BooksHandler(BookStore store) { this.store = store; }

        String handle(String method, String path, String body) {
            if (method.equals("POST") && path.equals("/books")) {
                NewBook input = BookParser.parse(body);
                String problem = BookRules.problem(input);
                if (problem != null) return "400 " + problem;
                return "201 " + BookJson.write(store.add(input.title(), input.year()));
            }
            if (method.equals("GET") && path.startsWith("/books/")) {
                try {
                    Book b = store.find(Long.parseLong(path.substring("/books/".length())));
                    return b == null ? "404" : "200 " + BookJson.write(b);
                } catch (NumberFormatException e) { return "404"; }
            }
            return "404";
        }
    }

    public static void main(String[] args) {
        List<String[]> script = List.of(
                new String[] {"POST", "/books", "Dune, 1965"},
                new String[] {"POST", "/books", " , 1965"},
                new String[] {"POST", "/books", "Beowulf, 1000"},
                new String[] {"GET", "/books/1", ""},
                new String[] {"GET", "/books/7", ""});
        BooksHandlerBefore before = new BooksHandlerBefore();
        BooksHandler after = new BooksHandler(new BookStore());
        for (String[] r : script) {
            String x = before.handle(r[0], r[1], r[2]);
            String y = after.handle(r[0], r[1], r[2]);
            System.out.println(String.format("%-13s", r[0] + " " + r[1]) + " -> " + y + (x.equals(y) ? "" : "   DIFFERENT: " + x));
        }
    }
}
