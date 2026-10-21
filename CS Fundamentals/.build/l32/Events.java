import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

public class Events {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Borrowed.class, name = "borrowed"),
        @JsonSubTypes.Type(value = Returned.class, name = "returned")})
    public sealed interface Event permits Borrowed, Returned {}

    public record Borrowed(@JsonProperty("book_id") long bookId, String member) implements Event {}
    public record Returned(@JsonProperty("book_id") long bookId, int lateDays) implements Event {}

    public static void main(String[] args) {
        var mapper = JsonMapper.builder().build();
        List<Event> events = List.of(new Borrowed(7, "ada"), new Returned(7, 2));
        String text = mapper.writerFor(new TypeReference<List<Event>>() {}).writeValueAsString(events);
        System.out.println(text);

        // List<Event>.class doesn't exist (erasure, L13): pass a type token instead
        List<Event> back = mapper.readValue(text, new TypeReference<List<Event>>() {});
        for (Event e : back) {
            String line = switch (e) {                // exhaustive: sealed (L7)
                case Borrowed b -> b.member() + " borrowed #" + b.bookId();
                case Returned r -> "#" + r.bookId() + " back, " + r.lateDays() + " days late";
            };
            System.out.println(line);
        }
    }
}
