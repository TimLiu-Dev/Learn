import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Practice {
    static final long TOLERANCE_SECONDS = 300;

    // The hex HMAC-SHA256 of message under key: 64 hex digits.
    static String hmacHex(byte[] key, String message) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
    }

    // The signature header a sender attaches: t=<timestamp>,v1=<tag of "t.body">
    static String sign(byte[] secret, long t, String body) throws Exception {
        return "t=" + t + ",v1=" + hmacHex(secret, t + "." + body);
    }

    // True only for a genuine, recent delivery. Never throws.
    static boolean verify(byte[] secret, String header, String body, long now) throws Exception {
        Long t = null;
        String tag = null;
        for (String entry : header.split(",")) {
            int eq = entry.indexOf('=');
            if (eq < 0) continue;
            String key = entry.substring(0, eq), value = entry.substring(eq + 1);
            if (key.equals("t")) {
                try { t = Long.parseLong(value); } catch (NumberFormatException e) { return false; }
            } else if (key.equals("v1")) {
                tag = value;
            }
        }
        if (t == null || tag == null) return false;
        byte[] expected = hmacHex(secret, t + "." + body).getBytes(StandardCharsets.UTF_8);   // the timestamp is signed too
        // Constant-time comparison: the time taken doesn't reveal how much of the tag was right.
        if (!MessageDigest.isEqual(expected, tag.getBytes(StandardCharsets.UTF_8))) return false;
        return Math.abs(now - t) <= TOLERANCE_SECONDS;     // a genuine but old delivery is a replay
    }

    // CHECKS
    static final byte[] SECRET = "whsec-for-tests-only-0123456789ab".getBytes(StandardCharsets.UTF_8);
    static final byte[] OTHER = "another-secret-nobody-should-use!".getBytes(StandardCharsets.UTF_8);
    static final long T = 1_767_225_600L;                  // 2026-01-01T00:00:00Z
    static final String BODY = "{\"id\":\"evt_1\",\"type\":\"payment.succeeded\",\"amount\":4200}";

    public static void main(String[] args) {
        check("a genuine, recent delivery verifies", true, () -> verify(SECRET, sign(SECRET, T, BODY), BODY, T + 10));
        check("the header is t, then v1 with 64 hex digits", true, () -> sign(SECRET, T, BODY).matches("t=1767225600,v1=[0-9a-f]{64}"));
        check("an edited body is rejected", false, () -> verify(SECRET, sign(SECRET, T, BODY), BODY.replace("4200", "999999"), T + 10));
        check("a delivery signed with another secret is rejected", false, () -> verify(SECRET, sign(OTHER, T, BODY), BODY, T + 10));
        check("a delivery 301 s old is rejected", false, () -> verify(SECRET, sign(SECRET, T, BODY), BODY, T + 301));
        check("300 s old is still inside the tolerance", true, () -> verify(SECRET, sign(SECRET, T, BODY), BODY, T + 300));
        check("a replay with a refreshed timestamp is rejected", false,
              () -> verify(SECRET, sign(SECRET, T, BODY).replace("t=" + T, "t=" + (T + 3600)), BODY, T + 3600));
        for (String h : List.of("", "v1=abc", "t=abc,v1=00", "t=1767225600", "t=1767225600,v1=not-hex"))
            check("malformed header '" + h + "' gives false", false, () -> verify(SECRET, h, BODY, T));
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
