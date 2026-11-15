import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Practice {
    static final String ALG = "HS256";            // the server picks the algorithm (RFC 8725 §3.1)
    static final String AUDIENCE = "books-api";   // this API's name: tokens for anyone else are rejected
    static final long LEEWAY = 60;                // seconds of clock skew allowed (RFC 7519 §4.1.4)

    // An allow-list of one: anything that isn't exactly ALG, including a missing alg, is rejected.
    static String headerProblem(Map<String, Object> header) {
        return ALG.equals(header.get("alg")) ? null : "alg must be HS256";
    }

    static String claimsProblem(Map<String, Object> claims, long now) {
        if (!(claims.get("exp") instanceof Long exp)) return "no exp";   // a string "exp" is not a number
        if (now >= exp + LEEWAY) return "expired";
        if (!AUDIENCE.equals(claims.get("aud"))) return "wrong audience";
        return null;
    }

    // CHECKS
    static final long NOW = 1_790_000_300L;

    static Map<String, Object> claims(Object... extra) {
        Map<String, Object> m = new HashMap<>(Map.of("iss", "https://books.example", "sub", "ana", "aud", "books-api", "exp", NOW + 600));
        for (int i = 0; i < extra.length; i += 2) {
            if (extra[i + 1] == null) m.remove((String) extra[i]); else m.put((String) extra[i], extra[i + 1]);
        }
        return m;
    }

    public static void main(String[] args) {
        check("a genuine header passes", null, () -> headerProblem(Map.of("alg", "HS256", "typ", "JWT")));
        check("genuine claims pass", null, () -> claimsProblem(claims(), NOW));
        for (String alg : List.of("none", "None", "HS512", "RS256", "hs256"))
            check("alg " + alg + " is rejected", "alg must be HS256", () -> headerProblem(Map.of("alg", alg, "typ", "JWT")));
        check("no alg at all is rejected", "alg must be HS256", () -> headerProblem(Map.of("typ", "JWT")));
        check("missing exp", "no exp", () -> claimsProblem(claims("exp", null), NOW));
        check("exp as a string", "no exp", () -> claimsProblem(claims("exp", "9999999999"), NOW));
        check("expired 30 s ago is within the leeway", null, () -> claimsProblem(claims("exp", NOW - 30), NOW));
        check("expired 60 s ago", "expired", () -> claimsProblem(claims("exp", NOW - 60), NOW));
        check("expired 10 min ago", "expired", () -> claimsProblem(claims("exp", NOW - 600), NOW));
        check("a token for billing-api", "wrong audience", () -> claimsProblem(claims("aud", "billing-api"), NOW));
        check("no aud", "wrong audience", () -> claimsProblem(claims("aud", null), NOW));
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
