// One row of the books table as a Java record.
// int can't hold null, so nullable columns need the boxed Integer.
public class RowTypes {
    record Book(int id, String title, Integer authorId, Integer year, int priceCents) {}

    public static void main(String[] args) {
        Book beowulf = new Book(19, "Beowulf", 7, null, 799);
        System.out.println(beowulf);
        System.out.println("age: " + (beowulf.year() == null ? "unknown" : 2026 - beowulf.year()));
    }
}
