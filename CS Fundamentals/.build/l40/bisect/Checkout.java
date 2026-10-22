public class Checkout {
    static long discountCents(long priceCents, long percent) {
        return priceCents * percent / 100;
    }

    static long lineTotal(long unitCents, int qty, long percent) {
        long gross = unitCents * qty;
        return gross - discountCents(gross, percent);
    }

    public static void main(String[] args) {
        long total = lineTotal(1000, 2, 10) + lineTotal(500, 2, 0);
        System.out.println("total: " + total + " cents");
    }
}
