import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

public class Builders {
    // Without a builder: new BookSearch("dune", null, 1960, 1970, "year", 20, 0). Which int is which?
    static final class BookSearch {
        private final String title, author, sort;
        private final int yearFrom, yearTo, limit, offset;

        private BookSearch(Builder b) {
            title = b.title; author = b.author; sort = b.sort;
            yearFrom = b.yearFrom; yearTo = b.yearTo; limit = b.limit; offset = b.offset;
        }
        static Builder builder() { return new Builder(); }

        @Override public String toString() {
            return "BookSearch(title=" + title + ", author=" + author + ", yearFrom=" + yearFrom + ", yearTo=" + yearTo
                + ", sort=" + sort + ", limit=" + limit + ", offset=" + offset + ")";
        }

        static final class Builder {
            private String title = "", author = null, sort = "title";   // defaults live in the builder
            private int yearFrom = 1400, yearTo = 2100, limit = 20, offset = 0;

            Builder title(String v) { title = v; return this; }         // each setter returns this: chaining
            Builder author(String v) { author = v; return this; }
            Builder years(int from, int to) { yearFrom = from; yearTo = to; return this; }
            Builder sort(String v) { sort = v; return this; }
            Builder page(int limit, int offset) { this.limit = limit; this.offset = offset; return this; }

            BookSearch build() {                                         // validate the whole object, once
                if (yearFrom > yearTo) throw new IllegalStateException("yearFrom " + yearFrom + " > yearTo " + yearTo);
                if (limit < 1 || limit > 100) throw new IllegalStateException("limit must be 1..100, was " + limit);
                return new BookSearch(this);
            }
        }
    }

    public static void main(String[] args) {
        BookSearch q = BookSearch.builder().title("dune").years(1960, 1970).sort("year").build();
        System.out.println(q);
        try { BookSearch.builder().years(2000, 1990).build(); }
        catch (IllegalStateException e) { System.out.println("rejected: " + e.getMessage()); }

        // Builders in the JDK: HttpRequest (L31) and StringBuilder
        HttpRequest req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:8080/books?q=dune"))
            .header("Accept", "application/json").timeout(Duration.ofSeconds(2)).GET().build();
        System.out.println(req.method() + " " + req.uri() + " " + req.headers().map());
        System.out.println(new StringBuilder().append("due: ").append("Dune").append(" (3 days)"));
    }
}
