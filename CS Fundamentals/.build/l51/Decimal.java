import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Decimal {
    // Accepts an optional sign, then digits, then optionally '.' and more digits.
    // "12", "-3.5", "+0.25" yes; "", "-", "1.", ".5", "--2", "1.2.3" no.
    enum St { START, SIGN, INT, DOT, FRAC }

    static St next(St q, char c) {                    // null = the dead state
        boolean digit = c >= '0' && c <= '9';   // not Character.isDigit: see below
        return switch (q) {
            case START -> c == '+' || c == '-' ? St.SIGN : digit ? St.INT : null;
            case SIGN -> digit ? St.INT : null;
            case INT -> digit ? St.INT : c == '.' ? St.DOT : null;
            case DOT -> digit ? St.FRAC : null;
            case FRAC -> digit ? St.FRAC : null;
        };
    }

    static boolean isDecimal(String s) {
        St q = St.START;
        for (char c : s.toCharArray()) {
            q = next(q, c);
            if (q == null) return false;
        }
        return q == St.INT || q == St.FRAC;
    }

    public static void main(String[] args) {
        String[] cases = {"12", "-3.5", "+0.25", "007", "", "-", "1.", ".5", "--2", "1.2.3", "\u0663"};
        for (String s : cases) System.out.printf("%-8s %s%n", "\"" + s + "\"", isDecimal(s));
        // Cross-check against a regex for the same language, on every string of length <= 5
        String alphabet = "1+-.\u0663";               // includes an Arabic-Indic three
        Pattern regex = Pattern.compile("[+-]?\\d+(\\.\\d+)?");
        List<String> all = List.of("");
        int checked = 0;
        for (int len = 1; len <= 5; len++) {
            List<String> longer = new ArrayList<>();
            for (String p : all) for (char c : alphabet.toCharArray()) longer.add(p + c);
            all = longer;
            for (String s : all) {
                checked++;
                if (isDecimal(s) != regex.matcher(s).matches()) throw new IllegalStateException(s);
            }
        }
        System.out.println("agrees with the regex on all " + checked + " strings");
    }
}
