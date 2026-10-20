import java.util.Map;
import java.util.Objects;

public class TryExpr {
    static int parseOr(String text, int fallback) {
        int n;                               // try is a statement: assign in each branch
        try {
            n = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            n = fallback;
        }
        return n;
    }

    public static void main(String[] args) {
        System.out.println(parseOr("42", -1));
        System.out.println(parseOr("4 2", -1));

        Map<String, String> settings = Map.of("host", "localhost");
        String host = settings.get("host");
        if (host == null) throw new IllegalArgumentException("host is required");
        System.out.println(host);
        String port = Objects.requireNonNull(settings.get("port"), "port is required");
        System.out.println(port);            // never reached
    }
}
