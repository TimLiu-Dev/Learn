public class Book {
    final String title;     // fields (we'll make them private in Part 2)
    final String author;
    int pages;

    Book(String title, String author, int pages) {
        if (pages <= 0) throw new IllegalArgumentException("pages must be positive");
        this.title = title;          // this.title is the field, title is the parameter
        this.author = author;
        this.pages = pages;
        System.out.println("created '" + title + "'");
    }

    Book(String title, String author) {
        this(title, author, 100);    // chain to the full constructor
    }

    Book(String title, int pages) {
        this(title, "Unknown", pages);
    }

    Book(Book other) {               // copy constructor
        this(other.title, other.author, other.pages);
    }

    public static void main(String[] args) {
        Book a = new Book("Dune", "Herbert", 412);
        Book b = new Book("Emma", "Austen");
        Book c = new Book(a);
        Book d = new Book("Ulysses", 730);
        c.pages = 500;
        System.out.println(a.title + " by " + a.author + ", " + a.pages + " pages");
        System.out.println(b.title + ": " + b.pages + " pages");
        System.out.println(c.title + " (copy): " + c.pages + " pages");
        System.out.println(d.title + " by " + d.author + ", " + d.pages + " pages");
    }
}
