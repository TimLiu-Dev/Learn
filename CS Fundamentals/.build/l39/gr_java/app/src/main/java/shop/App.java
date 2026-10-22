package shop;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        List<String> log = new ArrayList<>();
        OrderEvents events = new OrderEvents();
        // The composition root wires the patterns together
        Shop shop = new Shop(PricingRule.STANDARD, events,
            config -> new LoggingNotifier(new RetryingNotifier(Notifiers.from(config), 3), log));

        try (AutoCloseable audit = events.subscribe(e -> System.out.println("  audit: " + e))) {
            List<Order> orders = List.of(
                Order.builder("A1", "ada@uni.test").line("book", 2, 1250).line("pen", 10, 99).member(true).build(),
                Order.builder("B2", "+1-555-0100").line("book", 1, 1250).coupon("SAVE5").notifyBy("sms:2").build());
            for (Order order : orders) {
                System.out.println("placing " + order.id() + " (subtotal " + PricingRule.cents(order.subtotalCents()) + ")");
                System.out.println("total " + PricingRule.cents(shop.place(order)));
            }
        }
        System.out.println("notifier log: " + log);
    }
}
