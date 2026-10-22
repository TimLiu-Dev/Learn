package pricing;

import java.util.List;

public final class Pricing {
    public static int price(List<int[]> items, String coupon, boolean member) {
        int totalCents = 0;
        for (int[] item : items) {
            int quantity = item[0], unitCents = item[1];
            if (quantity <= 0) continue;
            totalCents += quantity * unitCents;
        }
        if (member) {
            if (totalCents > 5000) {
                totalCents = totalCents - totalCents * 10 / 100;
            } else {
                totalCents = totalCents - totalCents * 5 / 100;
            }
        }
        if (coupon != null) {
            if (coupon.equals("SAVE10")) {
                int discount = totalCents * 10 / 100;
                if (discount > 2000) discount = 2000;
                totalCents = totalCents - discount;
            } else if (coupon.equals("SAVE20")) {
                int discount = totalCents * 20 / 100;
                if (discount > 2000) discount = 2000;
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
        if (totalCents < 3500) {
            if (!member) totalCents = totalCents + 499;
        }
        return totalCents;
    }
}
