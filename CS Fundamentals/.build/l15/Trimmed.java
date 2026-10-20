import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Trimmed {
    static class Profile {
        private String name = "";
        private String city = "";
        private String initials;                        // lazy: null until first asked

        String getName() { return name; }
        void setName(String v) { name = v.trim(); }      // the trimming rule, repeated...
        String getCity() { return city; }
        void setCity(String v) { city = v.trim(); }      // ...in every setter

        String getInitials() {
            if (initials == null) {
                System.out.println("  (computing initials)");
                StringBuilder sb = new StringBuilder();
                for (String part : name.split(" ")) sb.append(part.charAt(0));
                initials = sb.toString();
            }
            return initials;
        }
    }

    interface Storage {
        void save(String key, String value);
        String load(String key);
        Set<String> keys();
    }

    static class MemoryStorage implements Storage {
        private final Map<String, String> data = new LinkedHashMap<>();
        public void save(String key, String value) { data.put(key, value); }
        public String load(String key) { return data.get(key); }
        public Set<String> keys() { return data.keySet(); }
    }

    static class LoggingStorage implements Storage {
        private final Storage inner;
        LoggingStorage(Storage inner) { this.inner = inner; }

        public void save(String key, String value) {
            System.out.println("  save " + key);
            inner.save(key, value);
        }
        public String load(String key) { return inner.load(key); }      // hand-written forwarding
        public Set<String> keys() { return inner.keys(); }               // hand-written forwarding
    }

    public static void main(String[] args) {
        Profile p = new Profile();
        p.setName("  Ada Lovelace ");
        p.setCity(" London");
        System.out.println("[" + p.getName() + "] [" + p.getCity() + "]");
        System.out.println("initials: " + p.getInitials());
        System.out.println("initials again: " + p.getInitials());

        Storage store = new LoggingStorage(new MemoryStorage());
        store.save("theme", "dark");
        store.save("lang", "en");
        System.out.println("theme=" + store.load("theme") + " keys=" + store.keys());
    }
}
