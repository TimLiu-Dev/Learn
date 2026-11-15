import java.util.List;
import java.util.Objects;

public class Practice {
    record Cart(int totalCents, int items, boolean member) {}

    @FunctionalInterface
    interface DiscountRule {
        int discountCents(Cart cart);
    }

    // Before: every new promotion means editing this method.
    static int discountFor(String promo, Cart cart) {
        return switch (promo) {
            case "MEMBER" -> cart.member() ? cart.totalCents() / 10 : 0;   // 10% off for members
            case "BULK" -> cart.items() >= 5 ? 200 : 0;                     // 2.00 off for 5 or more items
            default -> 0;
        };
    }

    static final class MemberDiscount implements DiscountRule {
        @Override public int discountCents(Cart cart) {
            throw new UnsupportedOperationException("TODO: move the MEMBER branch here");
        }
    }

    static final class BulkDiscount implements DiscountRule {
        @Override public int discountCents(Cart cart) {
            throw new UnsupportedOperationException("TODO: move the BULK branch here");
        }
    }

    static final class Checkout {
        private final List<DiscountRule> rules;
        Checkout(List<DiscountRule> rules) { this.rules = rules; }

        int total(Cart cart) {
            throw new UnsupportedOperationException("TODO: the cart's total minus every rule's discount, never below 0");
        }
    }

    // CHECKS
    // A promotion invented after Checkout was written.
    static final class FlatOff implements DiscountRule {
        private final int cents;
        FlatOff(int cents) { this.cents = cents; }
        @Override public int discountCents(Cart cart) { return cents; }
    }

    public static void main(String[] args) {
        check("members get 10 percent off", 4500, () -> new Checkout(List.of(new MemberDiscount())).total(new Cart(5000, 1, true)));
        check("a non-member pays full price", 5000, () -> new Checkout(List.of(new MemberDiscount())).total(new Cart(5000, 1, false)));
        check("five or more items take 200 cents off", 1800, () -> new Checkout(List.of(new BulkDiscount())).total(new Cart(2000, 5, false)));
        check("4 items is not bulk", 2000, () -> new Checkout(List.of(new BulkDiscount())).total(new Cart(2000, 4, false)));
        check("discounts add up", 4300, () -> new Checkout(List.of(new MemberDiscount(), new BulkDiscount())).total(new Cart(5000, 6, true)));
        check("a rule written later plugs in with no edits", 4200, () -> new Checkout(List.of(new MemberDiscount(), new FlatOff(300))).total(new Cart(5000, 1, true)));
        DiscountRule halfOffBigCarts = c -> c.totalCents() >= 10_000 ? c.totalCents() / 2 : 0;
        check("a lambda is a rule too", 6000, () -> new Checkout(List.of(halfOffBigCarts)).total(new Cart(12_000, 3, false)));
        check("the total never goes below zero", 0, () -> new Checkout(List.of(new FlatOff(1000))).total(new Cart(600, 1, false)));
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
