import java.util.ArrayList;
import java.util.List;

class Prices {
    /** @deprecated use {@link #formatPrice(int)} */
    @Deprecated(since = "2.0", forRemoval = true)
    static String oldPrice(int cents) { return "$" + cents / 100; }

    static String formatPrice(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }
}

public class Deprecation {
    @SuppressWarnings("removal")     // silence the warning for one deliberate call
    static String legacyReport() { return Prices.oldPrice(250); }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static List legacyList() {       // raw type on purpose (L13)
        List raw = new ArrayList();
        raw.add("x");
        return raw;
    }

    public static void main(String[] args) {
        System.out.println(Prices.oldPrice(1999));   // javac warns: marked for removal
        System.out.println(Prices.formatPrice(1999));
        System.out.println(legacyReport());
        System.out.println(legacyList());
    }
}
