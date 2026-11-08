import java.util.List;
import java.util.Objects;

public class Practice {
    interface Priced {
        String name();
        double price();
        default String label() { return name() + ": " + price(); }
    }

    abstract static class Product implements Priced {
        private final String name;
        private final double basePrice;
        Product(String name, double basePrice) { this.name = name; this.basePrice = basePrice; }
        abstract double taxRate();
        @Override public String name() { return name; }
        @Override public double price() { return basePrice * (1 + taxRate()); }
    }

    static final class Book extends Product {
        Book(String name, double basePrice) { super(name, basePrice); }
        @Override double taxRate() { return 0.0; }
    }

    static final class Gadget extends Product {
        Gadget(String name, double basePrice) { super(name, basePrice); }
        @Override double taxRate() { return 0.2; }
    }

    static List<String> labels(List<Priced> items) {
        return items.stream().map(Priced::label).toList();
    }

    // CHECKS
    public static void main(String[] args) {
        check("a book has no tax", "Dune: 10.0", () -> new Book("Dune", 10.0).label());
        check("a gadget adds 20 percent", 120.0, () -> new Gadget("Phone", 100.0).price());
        check("labels works on a list of Priced", List.of("Dune: 10.0", "Phone: 120.0"), () -> labels(List.of(new Book("Dune", 10.0), new Gadget("Phone", 100.0))));
        Priced giftCard = new Priced() {
            public String name() { return "Gift card"; }
            public double price() { return 25.0; }
        };
        check("anything Priced gets the default label", List.of("Gift card: 25.0"), () -> labels(List.of(giftCard)));
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
