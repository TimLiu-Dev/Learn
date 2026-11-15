import java.util.Objects;
import java.util.regex.Pattern;

public class Practice {
    // local part @ domain labels . a top-level domain of 2+ letters. Stops before trailing punctuation.
    static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}");

    // 13 to 19 digits with optional single separators. \b stops it matching inside a longer digit run.
    static final Pattern CARD = Pattern.compile("\\b\\d(?:[ -]?\\d){12,18}\\b");

    // Masks personal data in a log line before it's written.
    static String redact(String line) {
        String noEmails = EMAIL.matcher(line).replaceAll("[email]");
        return CARD.matcher(noEmails).replaceAll(m -> {
            String digits = m.group().replaceAll("\\D", "");
            return "[card ****" + digits.substring(digits.length() - 4) + "]";
        });
    }

    // CHECKS
    public static void main(String[] args) {
        check("an email is masked", "login ok for [email]", () -> redact("login ok for ana.lima@example.com"));
        check("punctuation after an email survives", "contact [email], then retry.", () -> redact("contact ana@x.com, then retry."));
        check("two emails on one line are masked separately", "from [email] to [email]", () -> redact("from a@x.io to b.c+tag@mail.example.org"));
        check("a card with spaces keeps its last four", "paid [card ****1111] ok", () -> redact("paid 4111 1111 1111 1111 ok"));
        check("a card with hyphens keeps its last four", "[card ****1881]", () -> redact("4012-8888-8888-1881"));
        check("a card with no separators keeps its last four", "card=[card ****0004]", () -> redact("card=5500000000000004"));
        String plain = "order 12345678 shipped 2026-11-14T09:30Z by build 2.4.21 (see @Test)";
        check("ids, dates, versions and a stray @ are left alone", plain, () -> redact(plain));
        String trace = "trace 123456789012345678901234 done";
        check("a 24-digit trace id is not a card", trace, () -> redact(trace));
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
