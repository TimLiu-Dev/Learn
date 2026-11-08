import java.util.*;

public class Practice {
    enum Coin {
        PENNY(1), NICKEL(5), DIME(10), QUARTER(25);

        final int cents;
        Coin(int cents) { this.cents = cents; }
    }

    static int total(List<Coin> coins) {
        throw new UnsupportedOperationException("TODO: add up every coin's cents");
    }

    static List<Coin> change(int amount) {
        throw new UnsupportedOperationException("TODO: fewest coins, largest first");
    }

    // CHECKS
    public static void main(String[] args) {
        check("total adds up each coin's cents", 37, () -> total(List.of(Coin.QUARTER, Coin.DIME, Coin.PENNY, Coin.PENNY)));
        check("no coins total 0", 0, () -> total(List.of()));
        check("41 cents is one of each coin, largest first", List.of(Coin.QUARTER, Coin.DIME, Coin.NICKEL, Coin.PENNY), () -> change(41));
        check("30 cents is a quarter and a nickel", List.of(Coin.QUARTER, Coin.NICKEL), () -> change(30));
        check("zero cents needs no coins", List.of(), () -> change(0));
        check("change always adds back up to the amount", true, () -> {
            for (int amount = 0; amount < 100; amount++) if (total(change(amount)) != amount) return false;
            return true;
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
