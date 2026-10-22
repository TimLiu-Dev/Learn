package shop;

import java.util.List;

// STRATEGY: one method, so every rule can be a lambda
@FunctionalInterface
public interface PricingRule {
    long apply(Order order, long total);

    PricingRule MEMBER_DISCOUNT = (order, total) -> order.member() ? total * 90 / 100 : total;
    PricingRule BULK_DISCOUNT = (order, total) -> order.itemCount() >= 10 ? total - 500 : total;
    PricingRule COUPON_SAVE5 = (order, total) -> "SAVE5".equals(order.coupon()) ? Math.max(0, total - 500) : total;
    List<PricingRule> STANDARD = List.of(MEMBER_DISCOUNT, BULK_DISCOUNT, COUPON_SAVE5);

    // The context applies whatever rules it is given, in order
    static long price(Order order, List<PricingRule> rules) {
        long total = order.subtotalCents();
        for (PricingRule rule : rules) total = rule.apply(order, total);
        return total;
    }

    static String cents(long c) { return String.format("$%d.%02d", c / 100, c % 100); }
}
