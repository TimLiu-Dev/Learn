import java.util.List;
import java.util.Map;

public class Lazy {
    static class Catalog {
        private List<String> products;                 // null means "not loaded yet"

        List<String> getProducts() {
            if (products == null) {                    // not thread-safe: fine for one thread only
                System.out.println("  (loading products...)");
                products = List.of("mat", "band", "kettlebell");
            }
            return products;
        }
    }

    // Holder-class idiom (Effective Java Item 83): the JVM initializes Holder on first use, once
    static class Config {
        private static class Holder {
            static final Map<String, String> VALUES = load();
        }

        static Map<String, String> values() { return Holder.VALUES; }

        private static Map<String, String> load() {
            System.out.println("  (loading config...)");
            return Map.of("mode", "dev");
        }
    }

    public static void main(String[] args) {
        Catalog c = new Catalog();
        System.out.println("catalog created");
        System.out.println("first access: " + c.getProducts().size());
        System.out.println("second access: " + c.getProducts().get(0));

        System.out.println("before config");
        System.out.println("mode = " + Config.values().get("mode"));
        System.out.println("mode again = " + Config.values().get("mode"));
    }
}
