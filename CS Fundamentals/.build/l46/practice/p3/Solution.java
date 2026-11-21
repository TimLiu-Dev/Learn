import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Practice {
    static class MissingConfig extends RuntimeException {
        MissingConfig(String message) { super(message); }
    }

    static class Settings {
        private final Map<String, String> values;
        private final Set<String> secrets;

        Settings(Map<String, String> values, Set<String> secrets) {
            this.values = values;
            this.secrets = secrets;
        }

        String get(String key) { return values.get(key); }

        // Logged at startup, so it must never print a secret.
        @Override public String toString() {
            return new TreeMap<>(values).entrySet().stream()
                .map(e -> e.getKey() + "=" + (secrets.contains(e.getKey()) ? "***" : e.getValue()))
                .collect(Collectors.joining(", "));
        }
    }

    // Each key takes its value from env first, then the file, then the defaults.
    // If any required key has no value anywhere, throw MissingConfig("missing config: A, B"),
    // naming every missing key in sorted order, so one failed start shows the whole list.
    static Settings loadSettings(Map<String, String> env, Map<String, String> file, Map<String, String> defaults,
                                 Set<String> required, Set<String> secrets) {
        Map<String, String> merged = new HashMap<>(defaults);   // weakest layer first;
        merged.putAll(file);                                    // each putAll overwrites what came before
        merged.putAll(env);
        List<String> missing = required.stream().filter(k -> !merged.containsKey(k)).sorted().toList();
        if (!missing.isEmpty()) throw new MissingConfig("missing config: " + String.join(", ", missing));
        return new Settings(merged, secrets);
    }

    // CHECKS
    static final Map<String, String> DEFAULTS = Map.of("PORT", "8080", "POOL", "8", "CACHE", "1000");

    public static void main(String[] args) {
        check("env beats the file, and the file beats the defaults", List.of("9000", "4", "1000"), () -> {
            Settings s = loadSettings(Map.of("PORT", "9000"), Map.of("PORT", "8081", "POOL", "4"), DEFAULTS, Set.of(), Set.of());
            return List.of(s.get("PORT"), s.get("POOL"), s.get("CACHE"));
        });
        check("a key from any layer is kept", List.of("1", "2", "3"), () -> {
            Settings s = loadSettings(Map.of("A", "1"), Map.of("B", "2"), Map.of("C", "3"), Set.of(), Set.of());
            return List.of(s.get("A"), s.get("B"), s.get("C"));
        });
        check("missing required keys fail fast, and the message names all of them", "missing config: DB_PASSWORD, DB_URL", () -> {
            try {
                loadSettings(Map.of(), Map.of(), DEFAULTS, Set.of("DB_URL", "PORT", "DB_PASSWORD"), Set.of());
                return "no exception";
            } catch (MissingConfig e) {
                return e.getMessage();
            }
        });
        check("a required key may come from any layer", "jdbc:sqlite:shelf.db", () ->
            loadSettings(Map.of(), Map.of("DB_URL", "jdbc:sqlite:shelf.db"), DEFAULTS, Set.of("DB_URL", "PORT"), Set.of()).get("DB_URL"));
        check("toString is sorted and never shows a secret", "DB_PASSWORD=***, POOL=8, PORT=9000 | hunter2", () -> {
            Settings s = loadSettings(Map.of("DB_PASSWORD", "hunter2", "PORT", "9000"), Map.of(), Map.of("POOL", "8"),
                                      Set.of("DB_PASSWORD"), Set.of("DB_PASSWORD"));
            return s + " | " + s.get("DB_PASSWORD");
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
