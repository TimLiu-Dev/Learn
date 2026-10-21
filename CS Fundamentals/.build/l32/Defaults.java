import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.json.JsonMapper;

public class Defaults {
    public record Settings(String email, String theme, Integer fontSize, String nickname) {
        public Settings {                              // defaults by hand, in a compact constructor
            if (theme == null) theme = "light";
            if (fontSize == null) fontSize = 14;
        }
    }

    public static void main(String[] args) {
        var mapper = JsonMapper.builder().build();
        var s = new Settings(null, "dark", null, null);
        System.out.println(mapper.writeValueAsString(s));             // nulls written

        var noNulls = JsonMapper.builder()
            .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
            .build();
        System.out.println(noNulls.writeValueAsString(s));            // nulls skipped

        Settings parsed = mapper.readValue("{\"email\":\"ada@example.com\"}", Settings.class);
        System.out.println(parsed);                                   // missing keys -> null -> defaults
    }
}
