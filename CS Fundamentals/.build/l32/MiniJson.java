import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

// A JSON subset parser: one method per grammar rule, each calling the others (L18 recursion).
public class MiniJson {
    private final String s;
    private int i = 0;

    MiniJson(String s) { this.s = s; }

    Object parse() {
        Object v = value();
        skipWs();
        if (i != s.length()) throw new IllegalArgumentException("trailing data at " + i);
        return v;
    }

    private Object value() {
        skipWs();
        char c = s.charAt(i);
        return switch (c) {
            case '{' -> obj();
            case '[' -> arr();
            case '"' -> str();
            case 't' -> word("true", true);
            case 'f' -> word("false", false);
            case 'n' -> word("null", null);
            default -> {
                if (c == '-' || Character.isDigit(c)) yield num();
                throw new IllegalArgumentException("unexpected '" + c + "' at " + i);
            }
        };
    }

    private Map<String, Object> obj() {
        var m = new LinkedHashMap<String, Object>();
        expect('{'); skipWs();
        if (s.charAt(i) == '}') { i++; return m; }
        while (true) {
            skipWs(); String k = str(); skipWs(); expect(':');
            m.put(k, value()); skipWs();
            if (s.charAt(i) == ',') i++; else { expect('}'); return m; }
        }
    }

    private List<Object> arr() {
        var l = new ArrayList<Object>();
        expect('['); skipWs();
        if (s.charAt(i) == ']') { i++; return l; }
        while (true) {
            l.add(value()); skipWs();
            if (s.charAt(i) == ',') i++; else { expect(']'); return l; }
        }
    }

    private String str() {
        expect('"');
        var sb = new StringBuilder();
        while (s.charAt(i) != '"') {
            if (s.charAt(i) == '\\') {
                i++;
                char e = s.charAt(i);
                sb.append(e == 'n' ? '\n' : e == 't' ? '\t' : e);    // \" \\ \/ pass through
            } else sb.append(s.charAt(i));
            i++;
        }
        i++;
        return sb.toString();
    }

    private Object num() {
        int start = i;
        while (i < s.length() && (Character.isDigit(s.charAt(i)) || "-+.eE".indexOf(s.charAt(i)) >= 0)) i++;
        String text = s.substring(start, i);
        return text.matches(".*[.eE].*") ? (Object) Double.parseDouble(text) : (Object) Long.parseLong(text);
    }

    private Object word(String w, Object v) {
        if (!s.startsWith(w, i)) throw new IllegalArgumentException("bad literal at " + i);
        i += w.length();
        return v;
    }
    private void expect(char c) {
        if (s.charAt(i) != c) throw new IllegalArgumentException("expected '" + c + "' at " + i);
        i++;
    }
    private void skipWs() { while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++; }

    // Turn the library's tree into the same plain values, so the two results can be compared.
    static Object plain(JsonNode n) {
        if (n.isNull()) return null;
        if (n.isObject()) {
            var m = new LinkedHashMap<String, Object>();
            for (var e : n.properties()) m.put(e.getKey(), plain(e.getValue()));
            return m;
        }
        if (n.isArray()) {
            var l = new ArrayList<Object>();
            for (JsonNode x : n) l.add(plain(x));
            return l;
        }
        if (n.isString()) return n.asString();
        if (n.isBoolean()) return n.asBoolean();
        return n.isIntegralNumber() ? (Object) n.asLong() : (Object) n.asDouble();
    }

    public static void main(String[] args) {
        var mapper = JsonMapper.builder().build();
        List<String> inputs = List.of(
            "{\"id\": 7, \"title\": \"Dune\", \"tags\": [\"sci-fi\", \"classic\"]}",
            "[1, -2.5, 3e2, true, false, null]",
            "{\"quote\": \"she said \\\"hi\\\"\\nthen left\", \"path\": \"C:\\\\tmp\"}",
            "{\"a\": {\"b\": {\"c\": [[], {}]}}}",
            "  \"just a string\"  ");
        for (String text : inputs) {
            Object mine = new MiniJson(text).parse();
            Object lib = plain(mapper.readTree(text));
            System.out.println((Objects.equals(mine, lib) ? "same" : "DIFF") + ": " + mine);
        }
    }
}
