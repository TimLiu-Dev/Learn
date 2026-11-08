import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Practice {
    static final class Currency {
        private static final Map<String, Currency> CACHE = new HashMap<>();   // one map for the class

        final String code;

        private Currency(String code) { this.code = code; }

        static Currency of(String code) {
            String c = code.trim().toUpperCase();
            if (!c.matches("[A-Z]{3}")) throw new IllegalArgumentException("not a currency code: " + code);
            return CACHE.computeIfAbsent(c, Currency::new);                 // reuse it, or make it once
        }

        static int known() { return CACHE.size(); }
    }

    // CHECKS
    static String thrown(Check c) {
        try { c.get(); return "nothing"; } catch (Exception e) { return e.getClass().getSimpleName(); }
    }

    public static void main(String[] args) {
        check("of normalises the code", "EUR", () -> Currency.of(" eur ").code);
        check("the same code gives the same object", true, () -> Currency.of("usd") == Currency.of("USD"));
        check("different codes give different objects", false, () -> Currency.of("GBP") == Currency.of("JPY"));
        check("bad codes are rejected", "IllegalArgumentException IllegalArgumentException IllegalArgumentException",
            () -> thrown(() -> Currency.of("EURO")) + " " + thrown(() -> Currency.of("E1R")) + " " + thrown(() -> Currency.of("")));
        check("known counts distinct currencies", 1, () -> {
            int before = Currency.known();
            Currency.of("CHF");
            Currency.of("chf");
            return Currency.known() - before;
        });
        check("the constructor is private", true,
            () -> java.lang.reflect.Modifier.isPrivate(Currency.class.getDeclaredConstructor(String.class).getModifiers()));
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
