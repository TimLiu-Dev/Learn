import java.util.*;
import java.util.stream.Collectors;

public class Practice {
    // s as a JSON string literal, quotes included
    static String quote(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));   // any other control character
                    else sb.append(c);
                }
            }
        }
        return sb.append('"').toString();
    }

    // null, Boolean, Integer, Long, String, List and Map<String, ?> -> compact JSON text
    static String encode(Object v) {
        if (v == null) return "null";
        if (v instanceof Boolean || v instanceof Integer || v instanceof Long) return v.toString();
        if (v instanceof String s) return quote(s);
        if (v instanceof List<?> list)   // recursion handles nesting
            return list.stream().map(Practice::encode).collect(Collectors.joining(",", "[", "]"));
        if (v instanceof Map<?, ?> map)
            return map.entrySet().stream()
                    .map(e -> quote((String) e.getKey()) + ":" + encode(e.getValue()))
                    .collect(Collectors.joining(",", "{", "}"));
        throw new IllegalArgumentException("can't encode a " + v.getClass().getSimpleName());
    }

    // CHECKS
    public static void main(String[] args) {
        check("a plain string is just quoted", "\"Dune\"", () -> quote("Dune"));
        check("quotes and backslashes are escaped", "\"she said \\\"hi\\\" in C:\\\\tmp\"", () -> quote("she said \"hi\" in C:\\tmp"));
        check("control characters are escaped", "\"a\\nb\\tc\\u0001\"", () -> quote("a\nb\tc\u0001"));
        check("every control character has an escape", "\"\\r\\b\\f\\u001f é ✓\"", () -> quote("\r\b\f\u001f é ✓"));
        Map<String, Object> book = new LinkedHashMap<>();
        book.put("id", 7);
        book.put("title", "Dune");
        book.put("tags", Arrays.asList("sci-fi", null, true));
        book.put("extra", Map.of());
        check("nested lists and maps encode compactly", "{\"id\":7,\"title\":\"Dune\",\"tags\":[\"sci-fi\",null,true],\"extra\":{}}", () -> encode(book));
        check("map keys are escaped too", "{\"say \\\"hi\\\"\":1}", () -> encode(Map.of("say \"hi\"", 1)));
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
