import java.math.BigDecimal;

public class Money {
    public static void main(String[] args) {
        double total = 0.0;
        for (int i = 1; i <= 10; i++) total += 0.1;
        System.out.println(total);
        System.out.println(total == 1.0);

        BigDecimal exact = BigDecimal.ZERO;
        for (int i = 1; i <= 10; i++) exact = exact.add(new BigDecimal("0.1"));
        System.out.println(exact);
        System.out.println(exact.compareTo(BigDecimal.ONE) == 0);

        System.out.println(new BigDecimal(0.1));
        double tolerance = 1e-9;
        System.out.println(Math.abs(total - 1.0) < tolerance);
    }
}
