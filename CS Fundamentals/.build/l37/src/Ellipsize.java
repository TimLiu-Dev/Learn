public class Ellipsize {
    /**
     * Shortens {@code text} to at most {@code max} characters, ending in "…" if anything was cut.
     * Never throws: a {@code max} of 0 or less gives "".
     */
    static String ellipsize(String text, int max) {
        if (max <= 0) return "";
        if (text.length() <= max) return text;
        return text.substring(0, max - 1) + "…";
    }

    public static void main(String[] args) {
        String title = "The Left Hand of Darkness";
        for (int max : new int[] {40, 25, 12, 1, 0, -3}) {
            System.out.println("max=" + max + " -> [" + ellipsize(title, max) + "]");
        }
    }
}
