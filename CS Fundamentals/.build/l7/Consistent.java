import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

public class Consistent {
    // Orders by price only, but equals (generated) compares name AND price
    record Product(String name, long cents) implements Comparable<Product> {
        public int compareTo(Product other) { return Long.compare(cents, other.cents); }
    }

    // Consistent: compareTo returns 0 exactly when equals is true
    record Item(String name, long cents) implements Comparable<Item> {
        public int compareTo(Item other) {
            int byPrice = Long.compare(cents, other.cents);
            return byPrice != 0 ? byPrice : name.compareTo(other.name);   // break ties
        }
    }

    public static void main(String[] args) {
        BigDecimal a = new BigDecimal("2.0");
        BigDecimal b = new BigDecimal("2.00");
        System.out.println(a.equals(b) + " " + a.compareTo(b));   // equals sees the scale, compareTo doesn't
        System.out.println(new HashSet<>(List.of(a, b)).size() + " " + new TreeSet<>(List.of(a, b)).size());

        Product tea = new Product("tea", 300);
        Product cake = new Product("cake", 300);
        System.out.println(new HashSet<>(List.of(tea, cake)).size() + " " + new TreeSet<>(List.of(tea, cake)).size());   // cake vanished
        System.out.println(new TreeSet<>(List.of(new Item("tea", 300), new Item("cake", 300))));
    }
}
