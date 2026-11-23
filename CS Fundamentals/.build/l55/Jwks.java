import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

public class Jwks {
    static final JsonMapper json = JsonMapper.builder().build();
    static final Base64.Encoder b64 = Base64.getUrlEncoder().withoutPadding();
    static final Base64.Decoder unb64 = Base64.getUrlDecoder();

    // RFC 7518 §6.3.1: n and e are unsigned big-endian bytes, base64url. toByteArray() may add a sign byte: drop it.
    static String b64(BigInteger i) {
        byte[] b = i.toByteArray();
        return b64.encodeToString(b[0] == 0 ? Arrays.copyOfRange(b, 1, b.length) : b);
    }

    static byte[] rs256(PrivateKey key, String input) {
        try {
            var s = Signature.getInstance("SHA256withRSA");
            s.initSign(key); s.update(input.getBytes(StandardCharsets.UTF_8)); return s.sign();
        } catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
    }

    static String jwt(ObjectNode header, ObjectNode claims, Function<String, byte[]> sign) {
        String input = b64.encodeToString(header.toString().getBytes(StandardCharsets.UTF_8)) + "."
                + b64.encodeToString(claims.toString().getBytes(StandardCharsets.UTF_8));
        return input + "." + b64.encodeToString(sign.apply(input));
    }

    static KeyPair rsaPair() {
        try {
            var g = KeyPairGenerator.getInstance("RSA"); g.initialize(2048); return g.generateKeyPair();   // RFC 7518 §3.3
        } catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
    }

    // The authorization server keeps the private keys and publishes only the public halves.
    static final class AuthServer {
        private final Map<String, KeyPair> keys = new LinkedHashMap<>();   // kid -> key pair
        private String current = "";
        void rotate(String kid) { keys.put(kid, rsaPair()); current = kid; }
        void retire(String kid) { keys.remove(kid); }
        RSAPublicKey publicKey(String kid) { return (RSAPublicKey) keys.get(kid).getPublic(); }
        String jwks() {                                                     // what GET /.well-known/jwks.json returns
            ObjectNode doc = json.createObjectNode();
            var arr = doc.putArray("keys");
            keys.forEach((kid, pair) -> {
                var pub = (RSAPublicKey) pair.getPublic();
                arr.addObject().put("kty", "RSA").put("kid", kid).put("use", "sig").put("alg", "RS256")
                        .put("n", b64(pub.getModulus())).put("e", b64(pub.getPublicExponent()));
            });
            return doc.toString();
        }
        String issue(ObjectNode claims) {
            ObjectNode h = json.createObjectNode().put("alg", "RS256").put("typ", "JWT").put("kid", current);
            return jwt(h, claims, in -> rs256(keys.get(current).getPrivate(), in));
        }
    }

    sealed interface Verdict permits Valid, Rejected {}
    record Valid(String subject) implements Verdict {}
    record Rejected(String reason) implements Verdict {}

    // The resource server holds no secret at all: only a cache of public keys, refreshed from the JWKS URL.
    static final class JwksVerifier {
        private final Supplier<String> fetchJwks;
        private final LongSupplier now;
        private final String issuer, audience;
        private Map<String, PublicKey> keys = Map.of();
        private long fetchedAt = Long.MIN_VALUE / 2;
        int fetches = 0;

        JwksVerifier(Supplier<String> fetchJwks, LongSupplier now, String issuer, String audience) {
            this.fetchJwks = fetchJwks; this.now = now; this.issuer = issuer; this.audience = audience;
        }

        private void refresh() throws GeneralSecurityException {
            if (now.getAsLong() - fetchedAt < 60) return;      // at most one fetch a minute: made-up kids can't flood the AS
            Map<String, PublicKey> fresh = new HashMap<>();
            for (JsonNode k : json.readTree(fetchJwks.get()).path("keys")) {
                var spec = new RSAPublicKeySpec(new BigInteger(1, unb64.decode(k.path("n").asString())),
                        new BigInteger(1, unb64.decode(k.path("e").asString())));
                fresh.put(k.path("kid").asString(), KeyFactory.getInstance("RSA").generatePublic(spec));
            }
            keys = fresh; fetchedAt = now.getAsLong(); fetches++;
        }

        private static JsonNode parse(String part) {
            try {
                var node = json.readTree(new String(unb64.decode(part), StandardCharsets.UTF_8));
                return node.isObject() ? node : null;
            } catch (RuntimeException e) { return null; }
        }

        Verdict verify(String token) throws GeneralSecurityException {
            var parts = token.split("\\.", -1);
            if (parts.length != 3) return new Rejected("not three parts");
            var header = parse(parts[0]);
            if (header == null) return new Rejected("header is not JSON");
            if (!"RS256".equals(header.path("alg").asString(null))) return new Rejected("alg must be RS256");   // still our choice
            String kid = header.path("kid").asString(null);
            if (kid == null) return new Rejected("no kid");
            if (now.getAsLong() - fetchedAt >= 300) refresh();  // cache for 5 minutes, like Spring's default
            PublicKey key = keys.get(kid);
            if (key == null) { refresh(); key = keys.get(kid); }
            if (key == null) return new Rejected("unknown kid");
            boolean ok;
            try {
                var s = Signature.getInstance("SHA256withRSA");
                s.initVerify(key); s.update((parts[0] + "." + parts[1]).getBytes(StandardCharsets.UTF_8));
                ok = s.verify(unb64.decode(parts[2]));
            } catch (GeneralSecurityException | IllegalArgumentException e) { ok = false; }
            if (!ok) return new Rejected("bad signature");
            var c = parse(parts[1]);                            // then Part 2's claim checks, unchanged
            if (c == null) return new Rejected("payload is not JSON");
            if (!c.path("exp").isNumber()) return new Rejected("no exp");
            if (now.getAsLong() >= c.path("exp").asLong() + 60) return new Rejected("expired");
            if (!issuer.equals(c.path("iss").asString(null))) return new Rejected("wrong issuer");
            if (!audience.equals(c.path("aud").asString(null))) return new Rejected("wrong audience");
            String sub = c.path("sub").asString(null);
            return sub == null ? new Rejected("no subject") : new Valid(sub);
        }
    }

    static long clock = 1_790_000_000L;

    public static void main(String[] args) throws Exception {
        ObjectNode claims = json.createObjectNode().put("iss", "https://as.example").put("sub", "ana")
                .put("aud", "photos-api").put("exp", clock + 3600);
        var as = new AuthServer();
        as.rotate("2026-11-a");
        var api = new JwksVerifier(as::jwks, () -> clock, "https://as.example", "photos-api");
        record Show(JwksVerifier api) {
            void line(String what, String token) throws GeneralSecurityException {
                System.out.printf("%-33s -> %-35s fetches: %d%n", what, api.verify(token), api.fetches);
            }
        }
        var show = new Show(api);

        System.out.println("JWKS: " + as.jwks().replaceAll("\"n\":\"(.{8})[^\"]*\"", "\"n\":\"$1…\""));
        String old = as.issue(claims);
        show.line("token signed with 2026-11-a", old);
        as.rotate("2026-12-b"); clock += 120;
        System.out.println("-- the AS publishes 2026-12-b and signs with it");
        show.line("token signed with 2026-12-b", as.issue(claims));
        show.line("older token, 2026-11-a", old);
        KeyPair attacker = rsaPair();
        ObjectNode rsHeader = json.createObjectNode().put("alg", "RS256").put("typ", "JWT").put("kid", "2026-12-b");
        show.line("forged: attacker's key, real kid", jwt(rsHeader, claims, in -> rs256(attacker.getPrivate(), in)));
        ObjectNode hsHeader = json.createObjectNode().put("alg", "HS256").put("kid", "2026-12-b");
        byte[] publicBytes = as.publicKey("2026-12-b").getEncoded();   // RFC 8725 §2.1: the public key as an HMAC secret
        show.line("forged: HS256 with the public key", jwt(hsHeader, claims, in -> {
            try {
                var mac = javax.crypto.Mac.getInstance("HmacSHA256");
                mac.init(new javax.crypto.spec.SecretKeySpec(publicBytes, "HmacSHA256"));
                return mac.doFinal(in.getBytes(StandardCharsets.UTF_8));
            } catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
        }));
        ObjectNode madeUp = json.createObjectNode().put("alg", "RS256").put("kid", "made-up");
        show.line("forged: a kid nobody published", jwt(madeUp, claims, in -> rs256(attacker.getPrivate(), in)));
        as.retire("2026-11-a"); clock += 360;
        System.out.println("-- the AS retires 2026-11-a; 6 minutes pass");
        show.line("older token, 2026-11-a", old);
    }
}
