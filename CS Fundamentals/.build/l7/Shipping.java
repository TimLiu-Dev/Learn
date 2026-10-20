public class Shipping {
    enum Method {
        STANDARD(5), EXPRESS(2), OVERNIGHT(1);

        private final int days;
        Method(int days) { this.days = days; }
        int days() { return days; }
    }

    static Method parse(String text) {
        try {
            return Method.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        for (Method m : Method.values()) System.out.println(m.name().toLowerCase() + ": " + m.days() + " days");
        System.out.println(parse("Express").days());
        Method unknown = parse("teleport");
        System.out.println(unknown == null ? "unknown method" : unknown.days());
    }
}
