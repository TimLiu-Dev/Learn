package pricing;

import java.util.List;

public final class Pricing {
    // calculates price
    public static int price(List<int[]> items, String coupon, boolean member) {
        int t = 0;
        for (int[] i : items) {
            if (i[0] <= 0) continue;
            t += i[0] * i[1];
        }
        if (member) {
            if (t > 5000) {
                t = t - t * 10 / 100;
            } else {
                t = t - t * 5 / 100;
            }
        }
        if (coupon != null) {
            if (coupon.equals("SAVE10")) {
                int d = t * 10 / 100;
                if (d > 2000) d = 2000;
                t = t - d;
            } else if (coupon.equals("SAVE20")) {
                int d = t * 20 / 100;
                if (d > 2000) d = 2000;
                t = t - d;
            } else if (coupon.startsWith("FLAT")) {
                int d;
                try {
                    d = Integer.parseInt(coupon.substring(4));
                } catch (NumberFormatException e) {
                    d = 0;
                }
                t = t - d * 100;
                if (t < 0) t = 0;
            }
        }
        if (t < 3500) {
            if (!member) t = t + 499;
        }
        return t;
    }
}
