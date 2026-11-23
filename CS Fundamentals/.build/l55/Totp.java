import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Totp {
    // HOTP (RFC 4226 §5.3): HMAC the 8-byte counter, then "dynamic truncation" down to a few decimal digits.
    static String hotp(byte[] secret, long counter, int digits) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(secret, "HmacSHA1"));
        byte[] hs = mac.doFinal(ByteBuffer.allocate(8).putLong(counter).array());
        int offset = hs[hs.length - 1] & 0x0f;                           // low 4 bits of the last byte pick a position
        int bin = ByteBuffer.wrap(hs, offset, 4).getInt() & 0x7fffffff;  // 4 bytes from there, top bit cleared: 31 bits
        int mod = 1;
        for (int i = 0; i < digits; i++) mod *= 10;
        return String.format("%0" + digits + "d", bin % mod);
    }

    // TOTP (RFC 6238 §4.2): the counter is the number of 30-second steps since the epoch. long, not int: 2038 is coming.
    static String totp(byte[] secret, long unixSeconds, int digits) throws Exception {
        return hotp(secret, unixSeconds / 30, digits);
    }

    public static void main(String[] args) throws Exception {
        byte[] secret = "12345678901234567890".getBytes(StandardCharsets.US_ASCII);   // RFC 6238 Appendix B's test secret
        List<Map.Entry<Long, String>> vectors = List.of(Map.entry(59L, "94287082"), Map.entry(1111111109L, "07081804"),
                Map.entry(1111111111L, "14050471"), Map.entry(1234567890L, "89005924"),
                Map.entry(2000000000L, "69279037"), Map.entry(20000000000L, "65353130"));
        for (var v : vectors) {
            String got = totp(secret, v.getKey(), 8);
            System.out.printf("%11d  T=%-9d %s  %s%n", v.getKey(), v.getKey() / 30, got,
                    got.equals(v.getValue()) ? "matches the RFC" : "WRONG, RFC says " + v.getValue());
        }
        var sb = new StringBuilder("6-digit codes at 1790000000..1790000089:");
        for (long d = 0; d <= 89; d += 30) sb.append(' ').append(totp(secret, 1_790_000_000L + d, 6));
        System.out.println(sb);
    }
}
