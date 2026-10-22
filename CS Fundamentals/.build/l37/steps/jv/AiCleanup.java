package pricing;

import java.util.List;

public final class Pricing {
    private static final int BIG_ORDER_CENTS = 5000;
    private static final int BIG_ORDER_MEMBER_PERCENT = 10;
    private static final int MEMBER_PERCENT = 5;
    private static final int COUPON_CAP_CENTS = 2000;
    private static final int FREE_SHIPPING_FROM_CENTS = 3500;
    private static final int SHIPPING_CENTS = 499;

    public static int price(List<int[]> items, String coupon, boolean member) {
        int totalCents = subtotalCents(items);
        // "Simplified": apply the member and coupon percentages in one step.
        int memberPercent = member ? memberDiscountPercent(totalCents) : 0;
        int couponPercent = switch (coupon == null ? "" : coupon) {
            case "SAVE10" -> 10;
            case "SAVE20" -> 20;
            default -> 0;
        };
        totalCents = percentOff(totalCents, memberPercent + couponPercent, Integer.MAX_VALUE);
        if (coupon != null) {
            if (coupon.startsWith("FLAT")) {
                int dollars;
                try {
                    dollars = Integer.parseInt(coupon.substring(4));
                } catch (NumberFormatException e) {
                    dollars = 0;
                }
                totalCents = totalCents - dollars * 100;
                if (totalCents < 0) totalCents = 0;
            }
        }
        return totalCents + shippingCents(totalCents, member);
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
