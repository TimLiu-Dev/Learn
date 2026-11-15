import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class Practice {
    static final String OUR_ORIGIN = "https://books.example";
    static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS");

    record Request(String method, String origin, String csrfHeader) {}

    // isEqual looks at every byte whatever it finds, so the time taken doesn't reveal the matching prefix.
    static boolean tokensMatch(String expected, String presented) {
        return presented != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), presented.getBytes(StandardCharsets.UTF_8));
    }

    static boolean csrfAllowed(Request req, String sessionToken) {
        if (SAFE_METHODS.contains(req.method())) return true;      // safe methods must never change state (L32)
        // Unsafe or unknown: deny unless both checks pass. Constant first, so a null Origin can't throw.
        return OUR_ORIGIN.equals(req.origin()) && tokensMatch(sessionToken, req.csrfHeader());
    }

    // CHECKS
    static final String TOKEN = "q7Xn2_hR9vLk0pZs4TbWm1yCe8uJd3fGa6iOx5NrVQE";

    static boolean anyAllowed(List<Request> requests) {
        return requests.stream().anyMatch(r -> csrfAllowed(r, TOKEN));
    }

    public static void main(String[] args) {
        check("our own page's POST with the token is allowed", true, () -> csrfAllowed(new Request("POST", OUR_ORIGIN, TOKEN), TOKEN));
        check("safe methods need neither Origin nor token", true, () -> Stream.of("GET", "HEAD", "OPTIONS").allMatch(m -> csrfAllowed(new Request(m, null, null), TOKEN)));
        check("a foreign or lookalike Origin is rejected, even with the right token", false, () -> anyAllowed(Stream.of("https://evil.example", "https://books.example.evil.example", "http://books.example", "null", null).map(o -> new Request("POST", o, TOKEN)).toList()));
        check("a missing, guessed or truncated token is rejected", false, () -> anyAllowed(Stream.of(null, "", "guess", TOKEN.substring(1), TOKEN + "A").map(t -> new Request("POST", OUR_ORIGIN, t)).toList()));
        check("PATCH and unknown methods count as unsafe", false, () -> anyAllowed(Stream.of("PATCH", "PUT", "DELETE", "PURGE").map(m -> new Request(m, OUR_ORIGIN, null)).toList()));
        check("tokensMatch: same text, different String object", true, () -> tokensMatch(TOKEN, new String(TOKEN.toCharArray())));
        check("tokensMatch: null, other case or a prefix never match", false, () -> tokensMatch(TOKEN, null) || tokensMatch(TOKEN, TOKEN.toUpperCase()) || tokensMatch(TOKEN, TOKEN.substring(0, 10)));
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
