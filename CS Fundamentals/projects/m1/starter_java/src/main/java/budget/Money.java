package budget;

import java.math.BigDecimal;

/**
 * An amount of money. Never a double (L2). Keep the invariant that the
 * BigDecimal always has scale 2, so 12.5 and 12.50 are equal records.
 * Add what you need: parsing, formatting, adding, comparing.
 */
public record Money(BigDecimal amount) {
    /** "12", "12.5" and "-12.50" are valid; anything else gives null. */
    public static Money parse(String text) {
        throw new UnsupportedOperationException("TODO: Milestone 1: parse an amount without going through double");
    }
}
