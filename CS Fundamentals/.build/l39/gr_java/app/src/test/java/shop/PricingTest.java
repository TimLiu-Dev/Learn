package shop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PricingTest {
    private static Order order(boolean member, String coupon, int qty) {
        return Order.builder("T", "t@test").line("x", qty, 1000).member(member).coupon(coupon).build();
    }

    @Test void noRulesGivesTheSubtotal() { assertEquals(3000, PricingRule.price(order(false, null, 3), List.of())); }

    @Test void membersGetTenPercentOff() {
        assertEquals(900, PricingRule.price(order(true, null, 1), List.of(PricingRule.MEMBER_DISCOUNT)));
    }

    @Test void tenOrMoreItemsTakeFiveDollarsOff() {
        assertEquals(9500, PricingRule.price(order(false, null, 10), List.of(PricingRule.BULK_DISCOUNT)));
    }

    @Test void couponNeverMakesTheTotalNegative() {
        Order cheap = Order.builder("T", "t@test").line("x", 1, 300).coupon("SAVE5").build();
        assertEquals(0, PricingRule.price(cheap, List.of(PricingRule.COUPON_SAVE5)));
    }

    @Test void ruleOrderMatters() {
        Order o = order(true, null, 10);                                                                   // subtotal 10000
        assertEquals(8500, PricingRule.price(o, List.of(PricingRule.MEMBER_DISCOUNT, PricingRule.BULK_DISCOUNT)));  // 9000 - 500
        assertEquals(8550, PricingRule.price(o, List.of(PricingRule.BULK_DISCOUNT, PricingRule.MEMBER_DISCOUNT)));  // 9500 * 0.9
    }
}
