// Exercise 1: scan a number that may use '_' between digits, like Kotlin and Java literals.
public class Digits {
    record Scanned(int value, int end) {}

    // src.charAt(start) must be a digit.
    static Scanned scanNumber(String src, int start) {
        int i = start;
        var digits = new StringBuilder();
        while (i < src.length()) {
            char c = src.charAt(i);
            if (c == '_') i++;
            else if (c >= '0' && c <= '9') { digits.append(c); i++; }
            else break;
        }
        if (src.charAt(i - 1) == '_') throw new CalcError("'_' must be followed by a digit at col " + i);
        try {
            return new Scanned(Integer.parseInt(digits.toString()), i);
        } catch (NumberFormatException e) {
            throw new CalcError("number too large at col " + (start + 1));
        }
    }

    public static void main(String[] args) {
        for (String s : new String[] {"1_000_000", "1__0", "7", "1_", "2_147_483_648", "12_3+4"}) {
            try {
                Scanned r = scanNumber(s, 0);
                System.out.printf("%-14s -> %d, rest \"%s\"%n", s, r.value(), s.substring(r.end()));
            } catch (CalcError e) {
                System.out.printf("%-14s -> CalcError: %s%n", s, e.getMessage());
            }
        }
    }
}
