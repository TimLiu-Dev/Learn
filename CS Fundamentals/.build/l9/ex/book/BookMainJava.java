public class BookMainJava {
    public static void main(String[] args) {
        Book book = Book.sample();
        System.out.println(book.getTitle());
        book.setAvailable(false);
        System.out.println("available: " + book.isAvailable());
        String subtitle = book.getSubtitle();
        System.out.println(subtitle != null ? subtitle : "(no subtitle)");
        System.out.println(subtitle != null ? subtitle.length() : 0);
    }
}
