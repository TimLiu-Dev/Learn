public class Checkout {
    static long discountCents(long priceCents, int percent) {
        return priceCents * percent / 100;
    }

    static long lineTotal(long priceCents, int qty, int percentOff) {
        long gross = priceCents * qty;
        long discount = discountCents(gross, percentOff);
        return gross - discount;
    }

    public static void main(String[] args) {
        long total = lineTotal(1000, 2, 10) + lineTotal(500, 2, 0);
        System.out.println("total: " + total + " cents");   // expected 2800
    }
}
