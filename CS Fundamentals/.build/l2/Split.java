import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Split {
    public static void main(String[] args) {
        BigDecimal bill = new BigDecimal("10.00");
        BigDecimal three = new BigDecimal("3");
        try {
            System.out.println(bill.divide(three));              // exact answer: 3.3333... forever
        } catch (ArithmeticException e) {
            System.out.println("ArithmeticException: " + e.getMessage());
        }
        BigDecimal share = bill.divide(three, 2, RoundingMode.HALF_EVEN);   // 2 decimal places
        System.out.println(share);
        System.out.println(share.multiply(three));               // a cent has gone missing

        // Fair split: work in whole cents, hand the leftover cents out one each
        long cents = bill.movePointRight(2).longValueExact();    // 1000
        long base = cents / 3;
        long leftover = cents % 3;
        long total = 0;
        for (int i = 1; i <= 3; i++) {
            long c = base + (i <= leftover ? 1 : 0);       // the first `leftover` people pay a cent more
            total += c;
            System.out.print(BigDecimal.valueOf(c, 2) + " ");   // c cents, shown with 2 decimals
        }
        System.out.println();
        System.out.println(BigDecimal.valueOf(total, 2));        // 10.00: nothing lost

        // Ties: HALF_EVEN rounds to the even neighbour, HALF_UP always up
        for (String s : List.of("2.345", "2.355")) {
            BigDecimal x = new BigDecimal(s);
            System.out.println(s + " -> " + x.setScale(2, RoundingMode.HALF_EVEN) + " " + x.setScale(2, RoundingMode.HALF_UP));
        }
    }
}
