package pricing;

import java.util.List;

public final class Pricing {
    private static final int BIG_ORDER_CENTS = 5000;
    private static final int BIG_ORDER_MEMBER_PERCENT = 10;
    private static final int MEMBER_PERCENT = 5;
    private static final int COUPON_CAP_CENTS = 2000;
    private static final int FREE_SHIPPING_FROM_CENTS = 3500;
    private static final int SHIPPING_CENTS = 499;

    public sealed interface Coupon permits PercentOff, FlatOff, NoCoupon {
        int apply(int totalCents);

        static Coupon parse(String code) {
            if ("SAVE10".equals(code)) return new PercentOff(10);
            if ("SAVE20".equals(code)) return new PercentOff(20);
            if (code != null && code.startsWith("FLAT")) return new FlatOff(dollarsOrZero(code.substring(4)) * 100);
            return new NoCoupon();
        }
    }

    public record PercentOff(int percent) implements Coupon {
        public int apply(int totalCents) { return percentOff(totalCents, percent, COUPON_CAP_CENTS); }
    }

    public record FlatOff(int cents) implements Coupon {
        public int apply(int totalCents) { return Math.max(0, totalCents - cents); }
    }

    public record NoCoupon() implements Coupon {
        public int apply(int totalCents) { return totalCents; }
    }

    public record Line(int quantity, int unitCents) {}

    public record Order(List<Line> lines, Coupon coupon, boolean isMember) {}

    public static int price(List<int[]> items, String coupon, boolean member) {
        return price(parseOrder(items, coupon, member));
    }

    // Phase 1: raw input -> clean model. Bad quantities are dropped here, once.
    public static Order parseOrder(List<int[]> items, String couponCode, boolean isMember) {
        List<Line> lines = items.stream()
                .filter(item -> item[0] > 0)
                .map(item -> new Line(item[0], item[1]))
                .toList();
        return new Order(lines, Coupon.parse(couponCode), isMember);
    }

    // Phase 2: arithmetic on the clean model. No strings, no validation.
    public static int price(Order order) {
        int totalCents = 0;
        for (Line line : order.lines()) totalCents += line.quantity() * line.unitCents();
        if (order.isMember()) totalCents = percentOff(totalCents, memberDiscountPercent(totalCents), Integer.MAX_VALUE);
        totalCents = order.coupon().apply(totalCents);
        return totalCents + shippingCents(totalCents, order.isMember());
    }

    private static int dollarsOrZero(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int memberDiscountPercent(int cents) {
        return cents > BIG_ORDER_CENTS ? BIG_ORDER_MEMBER_PERCENT : MEMBER_PERCENT;
    }

    private static int shippingCents(int cents, boolean isMember) {
        return cents < FREE_SHIPPING_FROM_CENTS && !isMember ? SHIPPING_CENTS : 0;
    }

    private static int percentOff(int cents, int percent, int capCents) {
        return cents - Math.min(cents * percent / 100, capCents);
    }
}
