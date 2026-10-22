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

    public static int price(List<int[]> items, String coupon, boolean member) {
        int totalCents = subtotalCents(items);
        if (member) totalCents = percentOff(totalCents, memberDiscountPercent(totalCents), Integer.MAX_VALUE);
        totalCents = Coupon.parse(coupon).apply(totalCents);
        return totalCents + shippingCents(totalCents, member);
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

    private static int shippingCents(int cents, boolean member) {
        return cents < FREE_SHIPPING_FROM_CENTS && !member ? SHIPPING_CENTS : 0;
    }

    private static int subtotalCents(List<int[]> items) {
        int cents = 0;
        for (int[] item : items) {
            int quantity = item[0], unitCents = item[1];
            if (quantity <= 0) continue;
            cents += quantity * unitCents;
        }
        return cents;
    }

    private static int percentOff(int cents, int percent, int capCents) {
        return cents - Math.min(cents * percent / 100, capCents);
    }
}
