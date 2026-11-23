import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Practice {
    static String command(String input) {
        Matcher m = Pattern.compile("^/(\\w+)").matcher(input.trim());
        if (!m.find()) return null;
        return m.group(1).toLowerCase(Locale.ROOT);   // plain toLowerCase() gives "quıt" in Turkey
    }

    static Integer seconds(String token) {
        Matcher m = Pattern.compile("(\\d+)([smh])").matcher(token);
        if (!m.matches()) return null;                // matches(): the whole token
        int n = Integer.parseInt(m.group(1));
        String unit = m.group(2);
        return unit.equals("s") ? n : unit.equals("m") ? n * 60 : n * 3600;
    }

    // CHECKS
    public static void main(String[] args) {
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));   // as on a laptop set up in Istanbul
        check("command reads the word after the slash", "mute", () -> command("/mute @ada 10m"));
        check("command ignores outer spaces and case", "mute", () -> command("  /MUTE @ada"));
        check("text without a leading slash is not a command", null, () -> command("hello /mute"));
        check("a lone slash is not a command", null, () -> command("/"));
        check("command works on a Turkish laptop", "quit", () -> command("/QUIT"));
        check("seconds understands s", 45, () -> seconds("45s"));
        check("seconds understands m", 600, () -> seconds("10m"));
        check("seconds understands h", 7200, () -> seconds("2h"));
        check("seconds rejects 10min", null, () -> seconds("10min"));
        check("seconds rejects -5m", null, () -> seconds("-5m"));
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
