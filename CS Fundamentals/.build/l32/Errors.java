import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

public class Errors {
    public record User(int id, String name) {}

    static void attempt(String label, JsonMapper mapper, String text) {
        try {
            System.out.println(label + " -> " + mapper.readValue(text, User.class));
        } catch (JacksonException e) {
            System.out.println(label + " -> " + e.getClass().getSimpleName() + ": " + e.getOriginalMessage());
        }
    }

    public static void main(String[] args) {
        var mapper = JsonMapper.builder().build();
        var strict = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
            .build();

        attempt("missing", mapper, "{\"id\":1}");
        attempt("missing, strict", strict, "{\"id\":1}");
        attempt("wrong type", mapper, "{\"id\":\"one\",\"name\":\"Ada\"}");
        attempt("unknown key", mapper, "{\"id\":1,\"name\":\"Ada\",\"admin\":true}");
        attempt("unknown, strict", strict, "{\"id\":1,\"name\":\"Ada\",\"admin\":true}");
    }
}
