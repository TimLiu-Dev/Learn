import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.LongSupplier;

public class Practice {
    // PKCE S256 (RFC 7636 §4.2): BASE64URL-ENCODE(SHA256(ASCII(code_verifier))), without padding.
    static String challengeOf(String verifier) {
        throw new UnsupportedOperationException("TODO: SHA-256 the ASCII bytes, then a base64url encoder without padding");
    }

    static class CodeStore {
        record Grant(String clientId, String redirectUri, String challenge, long issued) {}
        private final Map<String, Grant> grants = new HashMap<>();
        private final LongSupplier now;                               // seconds, injected so tests can move time

        CodeStore(LongSupplier now) { this.now = now; }

        // /authorize calls this after ana clicks Allow.
        void issue(String code, String clientId, String redirectUri, String challenge) {
            grants.put(code, new Grant(clientId, redirectUri, challenge, now.getAsLong()));
        }

        // /token calls this. True only if every check passes; either way, the code is used up.
        boolean redeem(String code, String clientId, String redirectUri, String verifier) {
            throw new UnsupportedOperationException("TODO: take the grant out of the map, then check age, client, redirect_uri and PKCE");
        }
    }

    // CHECKS
    static final String VERIFIER = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";   // RFC 7636 Appendix B
    static final String CHALLENGE = "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM";
    static final String APP = "photo-app", CALLBACK = "https://photo.example/callback", BAD = "x".repeat(43);
    static long clock = 1_790_000_000L;
    static final CodeStore store = new CodeStore(() -> clock);

    static String issued(String code) { store.issue(code, APP, CALLBACK, CHALLENGE); return code; }

    public static void main(String[] args) {
        check("challengeOf matches RFC 7636 Appendix B", CHALLENGE, () -> challengeOf(VERIFIER));
        check("the app that started the flow can redeem its code", true, () -> store.redeem(issued("a1"), APP, CALLBACK, VERIFIER));
        check("a made-up verifier is rejected", false, () -> store.redeem(issued("b1"), APP, CALLBACK, BAD));
        check("the challenge seen in the URL is no verifier", false, () -> store.redeem(issued("b2"), APP, CALLBACK, CHALLENGE));
        check("a code that was never issued is rejected", false, () -> store.redeem("b3", APP, CALLBACK, VERIFIER));
        check("a code works once", false, () -> { String c = issued("c1"); store.redeem(c, APP, CALLBACK, VERIFIER); return store.redeem(c, APP, CALLBACK, VERIFIER); });
        check("a failed attempt uses the code up too", false, () -> { String c = issued("c2"); store.redeem(c, APP, CALLBACK, BAD); return store.redeem(c, APP, CALLBACK, VERIFIER); });
        check("another client is rejected", false, () -> store.redeem(issued("d1"), "ad-app", CALLBACK, VERIFIER));
        check("a longer redirect_uri is rejected", false, () -> store.redeem(issued("d2"), APP, CALLBACK + "/../open?to=evil", VERIFIER));
        check("another host is rejected", false, () -> store.redeem(issued("d3"), APP, "https://evil.example/callback", VERIFIER));
        check("a 60 s old code still works", true, () -> { String c = issued("e1"); clock += 60; return store.redeem(c, APP, CALLBACK, VERIFIER); });
        check("a 61 s old code is rejected", false, () -> { String c = issued("e2"); clock += 61; return store.redeem(c, APP, CALLBACK, VERIFIER); });
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
