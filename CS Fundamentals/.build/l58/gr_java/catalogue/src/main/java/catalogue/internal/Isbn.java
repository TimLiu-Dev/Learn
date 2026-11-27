package catalogue.internal;

// A value object: equal when the digits are equal, and impossible to build invalid.
// ISBNs have 13 digits; the last is a check digit, "calculated using a Modulus 10 system
// with alternate weights of 1 and 3" (isbn-international.org).
public record Isbn(String digits) {
    public Isbn {
        if (!digits.matches("\\d{13}")) throw new IllegalArgumentException("an ISBN has 13 digits: '" + digits + "'");
        int sum = 0;
        for (int i = 0; i < 13; i++) sum += (digits.charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);
        if (sum % 10 != 0) throw new IllegalArgumentException("bad check digit: '" + digits + "'");
    }

    public static Isbn parse(String text) { return new Isbn(text.replace("-", "").replace(" ", "")); }
}
