import java.util.List;

public class Origins {
    public static void main(String[] args) {
        String expected = "https://books.example";
        List<String> signedOn = List.of("https://books.example", "https://evilbooks.example",
                "http://books.example", "https://books.example.evil");
        long exact = signedOn.stream().filter(o -> o.equals(expected)).count();
        long suffix = signedOn.stream().filter(o -> o.endsWith("books.example")).count();
        System.out.println("exact=" + exact + " suffix=" + suffix);
    }
}
