import java.util.Map;

public class Ex3 {
    static <T> T getAs(Map<String, Object> map, String key, Class<T> type) {
        Object value = map.get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }

    public static void main(String[] args) {
        Map<String, Object> settings = Map.of("port", 8080, "host", "localhost", "debug", true);
        Integer port = getAs(settings, "port", Integer.class);
        System.out.println(port);
        System.out.println(getAs(settings, "port", String.class));
        System.out.println(getAs(settings, "host", String.class));
        System.out.println(getAs(settings, "debug", Boolean.class));
        System.out.println(getAs(settings, "missing", Integer.class));
    }
}
