import java.util.List;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

public class Basics {
    public record Book(long id, String title, int year, List<String> tags, String subtitle) {}

    public static void main(String[] args) {
        var book = new Book(1, "Dune", 1965, List.of("sci-fi", "classic"), null);
        var mapper = JsonMapper.builder().build();

        String text = mapper.writeValueAsString(book);        // object -> JSON text
        System.out.println(text);

        Book back = mapper.readValue(text, Book.class);       // JSON text -> object
        System.out.println(back);
        System.out.println("equal after round trip: " + back.equals(book));

        var pretty = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();
        System.out.println(pretty.writeValueAsString(book));
    }
}
