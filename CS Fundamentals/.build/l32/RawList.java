import java.util.List;
import tools.jackson.databind.json.JsonMapper;

public class RawList {
    public record User(int id, String name) {}

    public static void main(String[] args) {
        var mapper = JsonMapper.builder().build();
        List<User> users = mapper.readValue("[{\"id\":1,\"name\":\"Ada\"}]", List.class);  // compiles (warning)
        Object first = users.get(0);                  // fine: no cast needed for Object
        System.out.println("element is a " + first.getClass().getSimpleName());
        User u = users.get(0);                        // the compiler inserts a cast to User here
        System.out.println(u.name());
    }
}
