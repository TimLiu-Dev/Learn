public class Price {
    static String formatPrice(long cents) {
        return formatPrice(cents, "$", true);
    }

    static String formatPrice(long cents, String symbol) {
        return formatPrice(cents, symbol, true);
    }

    static String formatPrice(long cents, String symbol, boolean showCents) {
        long whole = cents / 100;
        long rest = cents % 100;
        return showCents ? String.format("%s%d.%02d", symbol, whole, rest) : symbol + whole;
    }

    public static void main(String[] args) {
        System.out.println(formatPrice(1234));
        System.out.println(formatPrice(1205));
        System.out.println(formatPrice(1205, "$", false));
        System.out.println(formatPrice(99, "€"));
    }
}
