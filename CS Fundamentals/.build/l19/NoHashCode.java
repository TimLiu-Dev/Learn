import java.util.HashMap;
import java.util.Map;

public class NoHashCode {
    static final class Isbn {
        final String code;
        Isbn(String code) { this.code = code; }
        @Override public boolean equals(Object o) { return o instanceof Isbn i && i.code.equals(code); }
        // no hashCode(): every Isbn object keeps Object's identity-based hash
    }

    record FixedIsbn(String code) {}                // record: equals AND hashCode

    public static void main(String[] args) {
        Map<Isbn, Integer> prices = new HashMap<>();
        prices.put(new Isbn("978-0134685991"), 45);
        Isbn lookup = new Isbn("978-0134685991");
        System.out.println("equal keys? " + lookup.equals(new Isbn("978-0134685991")));
        System.out.println("price: " + prices.get(lookup));    // looks in the wrong bucket

        Map<FixedIsbn, Integer> fixed = new HashMap<>();
        fixed.put(new FixedIsbn("978-0134685991"), 45);
        System.out.println("fixed price: " + fixed.get(new FixedIsbn("978-0134685991")));
    }
}
