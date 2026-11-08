import java.util.List;
import java.util.Objects;

public class Practice {
    static class Item {
        final String name;
        Item(String name) { this.name = name; }
        String label() { return "item: " + name; }          // an instance method: overridable
    }

    static class Book extends Item {
        final String author;
        Book(String name, String author) { super(name); this.author = author; }
        @Override String label() { return "book: " + name + " by " + author; }
    }

    static List<String> labels(List<Item> items) {
        return items.stream().map(Item::label).toList();    // dispatched at runtime
    }

    static int bookCount(List<Item> items) {
        return (int) items.stream().filter(i -> i instanceof Book).count();
    }

    // CHECKS
    public static void main(String[] args) {
        check("a book on its own gets the book label", List.of("book: Dune by Herbert"), () -> labels(List.of(new Book("Dune", "Herbert"))));
        check("labels uses each item's real class", List.of("item: pen", "book: Dune by Herbert"), () -> labels(List.of(new Item("pen"), new Book("Dune", "Herbert"))));
        check("bookCount counts only books", 2, () -> bookCount(List.of(new Item("pen"), new Book("Dune", "Herbert"), new Item("lamp"), new Book("Emma", "Austen"))));
        check("an empty list has no books", 0, () -> bookCount(List.of()));
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
