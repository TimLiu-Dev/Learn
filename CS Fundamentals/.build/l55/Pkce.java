import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

// PKCE (RFC 7636): the app keeps a random secret (the verifier) and sends only its hash (the challenge).
public class Pkce {
    static final Base64.Encoder b64 = Base64.getUrlEncoder().withoutPadding();

    static String newVerifier() {                                        // §4.1
        var b = new byte[32];
        new SecureRandom().nextBytes(b);
        return b64.encodeToString(b);
    }
    static String challengeOf(String verifier) {                         // §4.2, S256
        try {
            return b64.encodeToString(MessageDigest.getInstance("SHA-256").digest(verifier.getBytes(StandardCharsets.US_ASCII)));
        } catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }

    public static void main(String[] args) {
        // RFC 7636 Appendix B: these 32 octets...
        int[] ints = {116, 24, 223, 180, 151, 153, 224, 37, 79, 250, 96, 125, 216, 173, 187, 186,
                22, 212, 37, 77, 105, 214, 191, 240, 91, 88, 5, 88, 83, 132, 141, 121};
        var octets = new byte[ints.length];
        for (int i = 0; i < ints.length; i++) octets[i] = (byte) ints[i];
        var verifier = b64.encodeToString(octets);
        System.out.println("verifier:  " + verifier);
        System.out.println("challenge: " + challengeOf(verifier));
        System.out.println("both match Appendix B: " + (verifier.equals("dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk")
                && challengeOf(verifier).equals("E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM")));

        var mine = newVerifier();
        boolean unreserved = mine.chars().allMatch(c -> Character.isLetterOrDigit(c) || "-._~".indexOf(c) >= 0);
        System.out.println("a fresh verifier has " + mine.length() + " chars, all unreserved: " + unreserved);
        System.out.println("its challenge: " + challengeOf(mine));
    }
}
