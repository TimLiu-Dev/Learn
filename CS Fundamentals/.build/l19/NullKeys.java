import java.util.HashMap;
import java.util.Map;

public class NullKeys {
    public static void main(String[] args) {
        Map<String, Integer> jdk = new HashMap<>();
        jdk.put(null, 0);                     // HashMap allows one null key...
        jdk.put("ghost", null);               // ...and null values
        System.out.println(jdk.get(null) + " " + jdk.get("ghost") + " " + jdk.get("missing"));
        System.out.println("containsKey: " + jdk.containsKey("ghost") + " " + jdk.containsKey("missing"));

        Map<String, Integer> ages = Map.of("ada", 36);
        Integer age = ages.get("bob");        // the type doesn't say whether null is possible
        System.out.println("bob: " + age + ", or default " + ages.getOrDefault("bob", -1));
    }
}
