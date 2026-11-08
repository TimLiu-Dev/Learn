import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Practice {
    static final class Currency {
        final String code;

        Currency(String code) { this.code = code; }     // TODO: outsiders must go through of()

        static Currency of(String code) {
            return new Currency(code);                   // TODO: normalise, validate, reuse
        }

        static int known() {
            throw new UnsupportedOperationException("TODO: how many distinct currencies exist so far");
        }
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
