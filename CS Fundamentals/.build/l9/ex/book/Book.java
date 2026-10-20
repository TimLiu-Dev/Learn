public class Book {
    private final String title;
    private final String subtitle;      // may be null
    private boolean available = true;

    public Book(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public static Book sample() { return new Book("Clean Code", null); }
}
