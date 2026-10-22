import java.util.List;

public class Ex1 {
    // The exercise snippet, as given:
    static int t(List<Integer> p, boolean m) {
        int s = 0;
        for (int i : p) s += i;       // add up
        if (m) s = s * 90 / 100;      // 10%
        return s;
    }

    // One good answer: two intention-revealing methods instead of a boolean flag.
    static final int MEMBER_PERCENT_OFF = 10;

    static int basketTotalCents(List<Integer> priceCents) {
        int subtotal = 0;
        for (int cents : priceCents) subtotal += cents;
        return subtotal;
    }

    // First try: looks equivalent... (it is not: different rounding)
    static int memberBasketTotalCentsFirstTry(List<Integer> priceCents) {
        int subtotal = basketTotalCents(priceCents);
        return subtotal - subtotal * MEMBER_PERCENT_OFF / 100;
    }

    /** Members pay (100 - {@link #MEMBER_PERCENT_OFF})% of the basket total, rounded down. */
    static int memberBasketTotalCents(List<Integer> priceCents) {
        int subtotal = basketTotalCents(priceCents);
        return subtotal * (100 - MEMBER_PERCENT_OFF) / 100;
    }

    public static void main(String[] args) {
        List<Integer> prices = List.of(1299, 850, 2000);
        System.out.println("member=false  old=" + t(prices, false) + "  new=" + basketTotalCents(prices));
        System.out.println("member=true  old=" + t(prices, true) + "  firstTry=" + memberBasketTotalCentsFirstTry(prices)
                + "  new=" + memberBasketTotalCents(prices));
    }
}
