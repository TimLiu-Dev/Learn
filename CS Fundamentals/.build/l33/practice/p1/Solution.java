import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Practice {
    // The hex HMAC-SHA256 of message under key: 64 hex digits.
    static String hmacHex(byte[] key, String message) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
    }

    static String sign(byte[] key, String message) throws Exception {
        return message + "." + hmacHex(key, message);
    }

    // The message if the tag is genuine; null for anything else. Never throws.
    static String verify(byte[] key, String token) throws Exception {
        int dot = token.lastIndexOf('.');         // the tag never contains a dot; the message might
        if (dot < 0) return null;
        String message = token.substring(0, dot);
        String tag = token.substring(dot + 1);
        byte[] expected = hmacHex(key, message).getBytes(StandardCharsets.UTF_8);
        // Constant-time comparison: the time taken doesn't reveal how much of the tag was right.
        return MessageDigest.isEqual(expected, tag.getBytes(StandardCharsets.UTF_8)) ? message : null;
    }

    // CHECKS
    static final byte[] KEY = "k3y-for-tests-only-0123456789abc".getBytes(StandardCharsets.UTF_8);
    static final byte[] OTHER_KEY = "another-key-nobody-should-trust!".getBytes(StandardCharsets.UTF_8);

    public static void main(String[] args) {
        check("a signed token verifies and gives back the message", "user=ana", () -> verify(KEY, sign(KEY, "user=ana")));
        check("the token is the message, a dot and 64 hex digits", true, () -> sign(KEY, "hi").matches("hi\\.[0-9a-f]{64}"));
        check("an edited message is rejected", null, () -> verify(KEY, sign(KEY, "user=ana;role=user").replace("role=user", "role=admin")));
        check("a token signed with another key is rejected", null, () -> verify(KEY, sign(OTHER_KEY, "user=ana")));
        check("a message may contain dots", "v1.2.3", () -> verify(KEY, sign(KEY, "v1.2.3")));
        check("a token with no dot gives null", null, () -> verify(KEY, "no-dot-here"));
        check("a non-hex tag gives null", null, () -> verify(KEY, "user=ana.not-hex"));
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
