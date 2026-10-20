package katas;

public class Price {
    public static String formatPrice(long cents) {
        return formatPrice(cents, "$", true);
    }

    public static String formatPrice(long cents, String symbol) {
        return formatPrice(cents, symbol, true);
    }

    public static String formatPrice(long cents, String symbol, boolean showCents) {
        long whole = cents / 100;
        long rest = cents % 100;
        return showCents ? String.format("%s%d.%02d", symbol, whole, rest) : symbol + whole;
    }
}
