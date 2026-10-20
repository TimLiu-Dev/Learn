public class Sizes {
    enum Size {
        SMALL(250),
        MEDIUM(350),
        LARGE(450);                   // semicolon: members follow

        private final int ml;

        Size(int ml) {                // enum constructors are always private
            this.ml = ml;
        }

        int ml() { return ml; }

        Size upsize() {
            return this == LARGE ? LARGE : values()[ordinal() + 1];
        }
    }

    static int price(Size size) {
        return switch (size) {        // exhaustive: no default needed
            case SMALL -> 300;
            case MEDIUM -> 350;
            case LARGE -> 400;
        };
    }

    public static void main(String[] args) {
        for (Size s : Size.values()) {
            System.out.println(s.name() + " #" + s.ordinal() + ": " + s.ml() + " ml, " + price(s) + " cents");
        }
        System.out.println(Size.SMALL.upsize());
        System.out.println(Size.LARGE.upsize());
        System.out.println(Size.valueOf("MEDIUM").ml());
        try {
            Size.valueOf("medium");   // names are case-sensitive
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(Size.MEDIUM == Size.valueOf("MEDIUM"));   // one object per constant
    }
}
