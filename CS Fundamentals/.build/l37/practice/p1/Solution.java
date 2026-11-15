import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Practice {
    record Book(String title, String author, int year) { }

    /** Kept for old callers: each mode now has its own method. */
    static List<String> bookLines(List<Book> books, boolean byAuthor) {
        return byAuthor ? authorLines(books) : yearLines(books);
    }

    /** "Author: Title" lines, sorted by author. */
    static List<String> authorLines(List<Book> books) {
        return books.stream()
                .sorted(Comparator.comparing(Book::author))
                .map(b -> b.author() + ": " + b.title())
                .toList();
    }

    /** "Title (year)" lines, oldest first. Books from the same year keep their order (sorted is stable here). */
    static List<String> yearLines(List<Book> books) {
        return books.stream()
                .sorted(Comparator.comparingInt(Book::year))
                .map(b -> b.title() + " (" + b.year() + ")")
                .toList();
    }

    // CHECKS
    public static void main(String[] args) {
        List<Book> shelf = List.of(new Book("Dune", "Herbert", 1965), new Book("Emma", "Austen", 1815), new Book("Ubik", "Dick", 1969));
        check("bookLines still gives the same output (by author)", List.of("Austen: Emma", "Dick: Ubik", "Herbert: Dune"), () -> bookLines(shelf, true));
        check("bookLines still gives the same output (by year)", List.of("Emma (1815)", "Dune (1965)", "Ubik (1969)"), () -> bookLines(shelf, false));
        check("authorLines sorts by author", List.of("Austen: Emma", "Dick: Ubik", "Herbert: Dune"), () -> authorLines(shelf));
        check("yearLines lists the oldest first", List.of("Emma (1815)", "Dune (1965)", "Ubik (1969)"), () -> yearLines(shelf));
        List<Book> sameYear = List.of(new Book("Ubik", "Dick", 1969), new Book("Kindred", "Butler", 1979), new Book("Solaris", "Lem", 1961), new Book("Slaughterhouse-Five", "Vonnegut", 1969));
        check("books from the same year keep their shelf order", List.of("Solaris (1961)", "Ubik (1969)", "Slaughterhouse-Five (1969)", "Kindred (1979)"), () -> yearLines(sameYear));
        check("an empty shelf gives no lines", List.of(), () -> yearLines(List.of()));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
