import java.util.List;
import tools.jackson.databind.json.JsonMapper;
public class RawList {
    public record User(int id, String name) {}
    public static void main(String[] a) {
        var mapper = JsonMapper.builder().build();
        List<User> users = mapper.readValue("[{\"id\":1,\"name\":\"Ada\"}]", List.class);
        System.out.println(users.get(0).getClass());
        User u = users.get(0);
    }
}
