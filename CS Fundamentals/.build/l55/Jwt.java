import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import tools.jackson.databind.json.JsonMapper;

// A JWT is three base64url parts: header.payload.signature (RFC 7515 §7.1, compact serialization).
public class Jwt {
    static final Base64.Encoder b64 = Base64.getUrlEncoder().withoutPadding();
    static final Base64.Decoder unb64 = Base64.getUrlDecoder();
    static final JsonMapper json = JsonMapper.builder().build();

    static byte[] hs256(byte[] key, String signingInput) {
        try {
            var mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(signingInput.getBytes(StandardCharsets.US_ASCII));
        } catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
    }

    static String sign(byte[] key, String header, String payload) {
        var input = b64.encodeToString(header.getBytes(StandardCharsets.UTF_8)) + "."
                + b64.encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return input + "." + b64.encodeToString(hs256(key, input));
    }

    static boolean signatureValid(byte[] key, String token) {
        var parts = token.split("\\.", -1);
        if (parts.length != 3) return false;
        var expected = hs256(key, parts[0] + "." + parts[1]);
        return MessageDigest.isEqual(expected, unb64.decode(parts[2]));   // constant time (L33)
    }

    public static void main(String[] args) {
        // 1. RFC 7515 Appendix A.1: the published key, signing input and signature
        var rfcKey = unb64.decode("AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow");
        var rfcInput = "eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9"
                + ".eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ";
        var ours = b64.encodeToString(hs256(rfcKey, rfcInput));
        System.out.println("RFC 7515 A.1 signature: " + ours);
        System.out.println("matches the RFC: " + ours.equals("dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"));

        // 2. Our own token, with a fresh 256-bit key (RFC 7518 §3.2: at least the hash size)
        var key = new byte[32];
        new SecureRandom().nextBytes(key);
        var header = json.createObjectNode().put("alg", "HS256").put("typ", "JWT").toString();
        var claims = json.createObjectNode()
                .put("iss", "https://books.example").put("sub", "ana").put("aud", "books-api")
                .put("iat", 1_790_000_000).put("exp", 1_790_000_900)            // 15 minutes later
                .put("role", "editor").toString();
        var token = sign(key, header, claims);
        System.out.println("token: " + token.substring(0, 40) + "…" + token.substring(token.length() - 12));
        System.out.println("valid signature: " + signatureValid(key, token));

        // 3. Signed is not secret: anyone can read the payload without the key
        var payload = json.readTree(new String(unb64.decode(token.split("\\.")[1]), StandardCharsets.UTF_8));
        System.out.println("anyone can read: sub=" + payload.get("sub") + " role=" + payload.get("role"));

        // 4. Change one claim and keep the old signature: the check fails
        var edited = claims.replace("\"editor\"", "\"admin\"");
        var p = token.split("\\.");
        var tampered = p[0] + "." + b64.encodeToString(edited.getBytes(StandardCharsets.UTF_8)) + "." + p[2];
        System.out.println("role edited to admin, old signature kept: valid=" + signatureValid(key, tampered));
    }
}
