import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class Plain {
    static String s256(String v) throws Exception {
        byte[] h = MessageDigest.getInstance("SHA-256").digest(v.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(h);
    }

    // The token endpoint's PKCE check, for each code_challenge_method.
    static boolean accepts(String method, String challenge, String verifier) throws Exception {
        return challenge.equals(method.equals("plain") ? verifier : s256(verifier));
    }

    public static void main(String[] args) throws Exception {
        String verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";   // stays inside the app
        String plainChallenge = verifier;                                   // what "plain" puts in the URL
        String s256Challenge = s256(verifier);                              // what "S256" puts in the URL
        // Someone who saw the URL presents the challenge they saw as the verifier.
        System.out.println(accepts("plain", plainChallenge, plainChallenge) + " "
                + accepts("S256", s256Challenge, s256Challenge));
    }
}
