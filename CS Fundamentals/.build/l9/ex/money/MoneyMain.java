public class MoneyMain {
    public static void main(String[] args) {
        Money m = Money.of(12.5);                    // needs @JvmStatic
        System.out.println(m.format());              // needs @JvmOverloads
        System.out.println(m.format("€"));
        System.out.println(m.format("€", false));
        System.out.println(MoneyUtils.cents(0.99));  // needs @file:JvmName
        System.out.println(m.getCents());            // property -> getter
    }
}
