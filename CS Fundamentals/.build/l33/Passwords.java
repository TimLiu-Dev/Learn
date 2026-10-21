import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Passwords {
    static final int ITERATIONS = 600_000;  // OWASP's PBKDF2-HMAC-SHA256 recommendation

    static byte[] pbkdf2(char[] password, byte[] salt, int iterations) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }

    // Stored form: algorithm$iterations$salt$hash, so you can raise the cost later
    static String hashPassword(char[] password) throws Exception {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(password, salt, ITERATIONS);
        Base64.Encoder b64 = Base64.getEncoder();
        return "pbkdf2-sha256$" + ITERATIONS + "$" + b64.encodeToString(salt) + "$" + b64.encodeToString(hash);
    }

    static boolean verifyPassword(char[] password, String stored) throws Exception {
        String[] p = stored.split("\\$");
        if (!p[0].equals("pbkdf2-sha256")) throw new IllegalArgumentException("unknown algorithm " + p[0]);
        Base64.Decoder unb64 = Base64.getDecoder();
        byte[] candidate = pbkdf2(password, unb64.decode(p[2]), Integer.parseInt(p[1]));
        return MessageDigest.isEqual(candidate, unb64.decode(p[3]));  // constant time
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 2; i++) hashPassword("warm-up".toCharArray());  // let the JIT settle (L23)
        long start = System.nanoTime();
        String stored = hashPassword("sunshine1".toCharArray());
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println("stored: " + stored.substring(0, 48) + "…");
        System.out.println("one hash took " + ms + " ms");
        System.out.println("right password: " + verifyPassword("sunshine1".toCharArray(), stored));
        System.out.println("wrong password: " + verifyPassword("sunshine2".toCharArray(), stored));
        System.out.println("same password again gives a new string: " + !hashPassword("sunshine1".toCharArray()).equals(stored));
    }
}
