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
        int totalCents = 0;
        for (int[] item : items) {
            int quantity = item[0], unitCents = item[1];
            if (quantity <= 0) continue;
            totalCents += quantity * unitCents;
        }
        if (member) {
            if (totalCents > BIG_ORDER_CENTS) {
                totalCents = totalCents - totalCents * BIG_ORDER_MEMBER_PERCENT / 100;
            } else {
                totalCents = totalCents - totalCents * MEMBER_PERCENT / 100;
            }
        }
        if (coupon != null) {
            if (coupon.equals("SAVE10")) {
                int discount = totalCents * 10 / 100;
                if (discount > COUPON_CAP_CENTS) discount = COUPON_CAP_CENTS;
                totalCents = totalCents - discount;
            } else if (coupon.equals("SAVE20")) {
                int discount = totalCents * 20 / 100;
                if (discount > COUPON_CAP_CENTS) discount = COUPON_CAP_CENTS;
                totalCents = totalCents - discount;
            } else if (coupon.startsWith("FLAT")) {
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
        if (totalCents < FREE_SHIPPING_FROM_CENTS) {
            if (!member) totalCents = totalCents + SHIPPING_CENTS;
        }
        return totalCents;
    }
}
