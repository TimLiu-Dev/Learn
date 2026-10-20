// Java has no extensions: the equivalent is a class of static helpers
public final class StringUtils {
    private StringUtils() {}   // no instances: it's only a holder for functions

    public static String initials(String s) {
        StringBuilder result = new StringBuilder();
        for (String word : s.split(" ")) {
            if (!word.isEmpty()) result.append(Character.toUpperCase(word.charAt(0)));
        }
        return result.toString();
    }

    public static char lastChar(String s) {
        return s.charAt(s.length() - 1);
    }

    public static String orDash(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}
