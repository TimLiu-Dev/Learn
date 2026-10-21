import tools.jackson.databind.json.JsonMapper;
public class Opaque {
    static class Secret { private int id = 1; }      // no getters, no annotations
    public static void main(String[] a) {
        System.out.println(JsonMapper.builder().build().writeValueAsString(new Secret()));
    }
}
