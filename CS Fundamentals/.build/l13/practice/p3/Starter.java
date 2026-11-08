import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Practice {
    // Java has no reified type parameters, so the caller passes a class token.
    static class Favorites {
        private final Map<Class<?>, Object> slots = new HashMap<>();

        <T> void put(Class<T> type, T value) {
            throw new UnsupportedOperationException("TODO: store value under type");
        }

        <T> T get(Class<T> type) {
            throw new UnsupportedOperationException("TODO: read type's slot, checked with the token");
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("what you put in comes back out", "Java", () -> {
            Favorites f = new Favorites();
            f.put(String.class, "Java");
            return f.get(String.class);
        });
        check("each type has its own slot", "tea 42 2.5", () -> {
            Favorites f = new Favorites();
            f.put(String.class, "tea");
            f.put(Integer.class, 42);
            f.put(Double.class, 2.5);
            return f.get(String.class) + " " + f.get(Integer.class) + " " + f.get(Double.class);
        });
        check("a type with nothing stored gives null", null, () -> new Favorites().get(Boolean.class));
        check("putting the same type again replaces the old value", 2, () -> {
            Favorites f = new Favorites();
            f.put(Integer.class, 1);
            f.put(Integer.class, 2);
            return f.get(Integer.class);
        });
        check("the slot is chosen by the token, not the runtime class", "hi null", () -> {
            Favorites f = new Favorites();
            f.put(CharSequence.class, "hi");
            return f.get(CharSequence.class) + " " + f.get(String.class);
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
