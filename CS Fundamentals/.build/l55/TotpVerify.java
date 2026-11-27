import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.function.LongSupplier;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TotpVerify {
    static String totp(byte[] secret, long step) {                       // RFC 6238 with 6 digits, as in Totp.java
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secret, "HmacSHA1"));
            byte[] hs = mac.doFinal(ByteBuffer.allocate(8).putLong(step).array());
            int offset = hs[hs.length - 1] & 0x0f;
            return String.format("%06d", (ByteBuffer.wrap(hs, offset, 4).getInt() & 0x7fffffff) % 1_000_000);
        } catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
    }

    static final class TotpVerifier {
        private final byte[] secret;
        private final LongSupplier now;
        private long lastUsedStep = Long.MIN_VALUE;
        TotpVerifier(byte[] secret, LongSupplier now) { this.secret = secret; this.now = now; }

        boolean verify(String code) {
            long current = now.getAsLong() / 30;
            for (long step : new long[] {current, current - 1}) {        // §5.2: "at most one time step" of delay
                if (step <= lastUsedStep) continue;                      // §5.2: never accept the same OTP twice
                if (MessageDigest.isEqual(totp(secret, step).getBytes(StandardCharsets.US_ASCII),
                        code.getBytes(StandardCharsets.US_ASCII))) {     // constant time (L33)
                    lastUsedStep = step;
                    return true;
                }
            }
            return false;
        }
    }

    static long clock = 1_790_000_010L;                                  // exactly where step 59666667 starts

    public static void main(String[] args) {
        byte[] secret = "12345678901234567890".getBytes(StandardCharsets.US_ASCII);
        var v = new TotpVerifier(secret, () -> clock);
        long step = clock / 30;
        System.out.println("code for this step           -> " + v.verify(totp(secret, step)));
        System.out.println("the same code again          -> " + v.verify(totp(secret, step)));
        clock += 30;
        System.out.println("next step, last step's code  -> " + new TotpVerifier(secret, () -> clock).verify(totp(secret, step)));
        System.out.println("next step, a fresh code      -> " + v.verify(totp(secret, step + 1)));
        System.out.println("a code from two steps ago    -> " + v.verify(totp(secret, step - 1)));
        System.out.println("a code from the future       -> " + v.verify(totp(secret, step + 2)));
        System.out.println("six wrong digits             -> " + v.verify("000000"));
    }
}
