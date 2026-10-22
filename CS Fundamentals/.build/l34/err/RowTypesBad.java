public class RowTypesBad {
    record Book(int id, String title, int year) {}

    public static void main(String[] args) {
        System.out.println(new Book(19, "Beowulf", null));
    }
}
