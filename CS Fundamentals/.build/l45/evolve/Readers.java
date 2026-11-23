import java.util.Objects;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

public class Readers {
    // What version 1.0 of a client app knows about a book. It was shipped months ago.
    record BookV1(long id, String title, String author, Integer year) {
        BookV1 {   // Java has no non-null types, so a required field is checked by hand
            Objects.requireNonNull(title, "title");
            Objects.requireNonNull(author, "author");
        }
    }

    static final JsonMapper STRICT = JsonMapper.builder()
        .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
    static final JsonMapper TOLERANT = JsonMapper.builder().build();   // the Jackson 3 default ignores unknown keys

    static void read(String label, JsonMapper reader, String body) {
        try {
            System.out.println(label + "  " + reader.readValue(body, BookV1.class));
        } catch (JacksonException e) {
            System.out.println(label + "  FAILED: " + e.getOriginalMessage().lines().findFirst().orElse(""));
        }
    }

    public static void main(String[] args) {
        String v1 = """
            {"id":1,"title":"Dune","author":"Frank Herbert","year":1965}""";
        String added = """
            {"id":1,"title":"Dune","author":"Frank Herbert","year":1965,"isbn":"9780441013593"}""";
        String renamed = """
            {"id":1,"title":"Dune","authors":["Frank Herbert"],"year":1965}""";
        read("strict,   server v1     ", STRICT, v1);
        read("strict,   field added   ", STRICT, added);
        read("tolerant, field added   ", TOLERANT, added);
        read("tolerant, field renamed ", TOLERANT, renamed);
    }
}
