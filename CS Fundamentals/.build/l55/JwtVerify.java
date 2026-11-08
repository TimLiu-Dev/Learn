import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.function.LongSupplier;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

// A strict verifier. Each check is a rule from RFC 7519 (JWT) or RFC 8725 (JWT Best Current Practices).
public class JwtVerify {
    sealed interface Verdict permits Valid, Rejected {}
    record Valid(String subject) implements Verdict {}
    record Rejected(String reason) implements Verdict {}

    static final JsonMapper json = JsonMapper.builder().build();
    static final Base64.Encoder b64 = Base64.getUrlEncoder().withoutPadding();
    static final Base64.Decoder unb64 = Base64.getUrlDecoder();

    static byte[] hs256(byte[] key, String input) {
        try {
            var mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
    }

    static final class JwtVerifier {
        private final byte[] key;
        private final String issuer, audience;
        private final LongSupplier now;                  // seconds since the epoch, injected for tests
        private final long leeway = 60;                  // RFC 7519 §4.1.4: "usually no more than a few minutes"

        JwtVerifier(byte[] key, String issuer, String audience, LongSupplier now) {
            this.key = key; this.issuer = issuer; this.audience = audience; this.now = now;
        }

        private static JsonNode parse(String part) {
            try {
                var node = json.readTree(new String(unb64.decode(part), StandardCharsets.UTF_8));
                return node.isObject() ? node : null;
            } catch (RuntimeException e) { return null; }
        }

        Verdict verify(String token) {
            var parts = token.split("\\.", -1);
            if (parts.length != 3) return new Rejected("not three parts");
            var header = parse(parts[0]);
            if (header == null) return new Rejected("header is not JSON");
            // We decide the algorithm; the token's header is only checked against it (RFC 8725 §3.1)
            if (!"HS256".equals(header.path("alg").asString(null))) return new Rejected("alg must be HS256");
            byte[] sig;
            try { sig = unb64.decode(parts[2]); } catch (IllegalArgumentException e) { return new Rejected("bad signature encoding"); }
            if (!MessageDigest.isEqual(hs256(key, parts[0] + "." + parts[1]), sig)) return new Rejected("bad signature");
            // Only now are the claims worth reading
            var c = parse(parts[1]);
            if (c == null) return new Rejected("payload is not JSON");
            if (!c.path("exp").isNumber()) return new Rejected("no exp");
            if (now.getAsLong() >= c.path("exp").asLong() + leeway) return new Rejected("expired");
            if (!issuer.equals(c.path("iss").asString(null))) return new Rejected("wrong issuer");
            if (!audience.equals(c.path("aud").asString(null))) return new Rejected("wrong audience");   // RFC 8725 §3.9
            var sub = c.path("sub").asString(null);
            return sub == null ? new Rejected("no subject") : new Valid(sub);
        }
    }

    // The issuer side, used here to make test tokens
    static String issue(byte[] key, ObjectNode header, ObjectNode claims) {
        var input = b64.encodeToString(header.toString().getBytes(StandardCharsets.UTF_8)) + "."
                + b64.encodeToString(claims.toString().getBytes(StandardCharsets.UTF_8));
        return input + "." + b64.encodeToString(hs256(key, input));
    }

    // RFC 7519 §6 allows "unsecured" JWTs with alg "none" and an empty signature. A server must never accept one.
    static String unsigned(String token) {
        return b64.encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8)) + "." + token.split("\\.")[1] + ".";
    }

    static final long T0 = 1_790_000_000L;
    static ObjectNode claims(long exp, String iss, String aud) {
        return json.createObjectNode().put("iss", iss).put("sub", "ana").put("aud", aud).put("exp", exp);
    }
    static ObjectNode claims() { return claims(T0 + 900, "https://books.example", "books-api"); }
    static byte[] randomKey() {
        var k = new byte[32];
        new SecureRandom().nextBytes(k);
        return k;
    }

    public static void main(String[] args) {
        var key = randomKey();
        var verifier = new JwtVerifier(key, "https://books.example", "books-api", () -> T0 + 300);
        var hs = json.createObjectNode().put("alg", "HS256").put("typ", "JWT");
        var good = issue(key, hs, claims());
        var cases = new LinkedHashMap<String, String>();
        cases.put("genuine token", good);
        cases.put("unsigned (alg none)", unsigned(good));
        cases.put("other algorithm", issue(key, json.createObjectNode().put("alg", "HS512"), claims()));
        cases.put("signed by another key", issue(randomKey(), hs, claims()));
        cases.put("expired 10 min ago", issue(key, hs, claims(T0 + 300 - 600, "https://books.example", "books-api")));
        cases.put("expired 30 s ago (leeway)", issue(key, hs, claims(T0 + 300 - 30, "https://books.example", "books-api")));
        cases.put("for another service", issue(key, hs, claims(T0 + 900, "https://books.example", "billing-api")));
        cases.put("from another issuer", issue(key, hs, claims(T0 + 900, "https://other.example", "books-api")));
        cases.forEach((name, token) -> System.out.printf("%-26s -> %s%n", name, verifier.verify(token)));
    }
}
