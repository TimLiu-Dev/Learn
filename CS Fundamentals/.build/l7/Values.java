import java.util.ArrayList;
import java.util.List;

public class Values {
    record Money(long cents, String currency) {
        Money {                                   // compact constructor: validate, fields assigned after
            if (cents < 0) throw new IllegalArgumentException("negative amount: " + cents);
        }
    }

    record Order(String id, List<String> items) {}

    public static void main(String[] args) {
        Money price = new Money(1250, "EUR");
        System.out.println(price);                               // generated toString
        System.out.println(price.cents() + " " + price.currency()); // accessors: cents(), not getCents()
        System.out.println(price.equals(new Money(1250, "EUR"))); // generated equals
        Money raised = new Money(price.cents() + 100, price.currency());  // no copy(): build a new one
        System.out.println(raised);

        try {
            new Money(-5, "EUR");
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }

        List<String> items = new ArrayList<>(List.of("tea"));
        Order order = new Order("A1", items);
        items.add("cake");                                       // the record's fields are final...
        System.out.println(order);                               // ...but the list inside still changes
    }
}
