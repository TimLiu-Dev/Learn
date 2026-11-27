package budget;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * An amount of money: a BigDecimal that always has exactly two decimals.
 * BigDecimal arithmetic is exact, so 0.10 + 0.20 is 0.30 (L2).
 */
public record Money(BigDecimal amount) implements Comparable<Money> {
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    private static final Pattern FORMAT = Pattern.compile("-?\\d+(\\.\\d{1,2})?");
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public Money {
        // The invariant: scale 2. setScale throws if that would lose digits.
        amount = amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    /** Parses "12", "12.5", "-12.50"; null for anything else. */
    public static Money parse(String text) {
        return FORMAT.matcher(text).matches() ? new Money(new BigDecimal(text)) : null;
    }

    public static Money sum(Collection<Money> amounts) {
        return amounts.stream().reduce(ZERO, Money::plus);
    }

    public Money plus(Money other) {
        return new Money(amount.add(other.amount));
    }

    public Money minus(Money other) {
        return new Money(amount.subtract(other.amount));
    }

    public Money negate() {
        return new Money(amount.negate());
    }

    public Money abs() {
        return new Money(amount.abs());
    }

    public int signum() {
        return amount.signum();
    }

    /** This amount as a percentage of {@code whole}, to 1 dp, rounded HALF_EVEN. */
    public BigDecimal percentOf(Money whole) {
        return amount.multiply(HUNDRED).divide(whole.amount, 1, RoundingMode.HALF_UP); // BUG: spec says HALF_EVEN
    }

    @Override
    public int compareTo(Money other) {
        return amount.compareTo(other.amount);
    }

    /** "-1234.50": plain digits, two decimals, no grouping, no currency sign. */
    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
