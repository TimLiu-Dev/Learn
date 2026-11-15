import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Practice {
    static byte[] pbkdf2(String pw, byte[] salt, int iterations) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(pw.toCharArray(), salt, iterations, 256);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
    }

    // pbkdf2-sha256$<iterations>$<base64 salt>$<base64 hash>
    static String store(String pw, byte[] salt, int iterations) throws Exception {
        Base64.Encoder b64 = Base64.getEncoder();
        return "pbkdf2-sha256$" + iterations + "$" + b64.encodeToString(salt) + "$" + b64.encodeToString(pbkdf2(pw, salt, iterations));
    }

    // True only if pw matches. Unknown or malformed input is false, never an exception.
    static boolean verify(String pw, String stored) throws Exception {
        throw new UnsupportedOperationException("TODO: split the four fields, check the algorithm, re-hash with the stored salt and iterations, compare");
    }

    // CHECKS
    static final byte[] SALT = "sixteen-byte-slt".getBytes(StandardCharsets.UTF_8);

    public static void main(String[] args) {
        check("the right password verifies", true, () -> verify("correct horse", store("correct horse", SALT, 1000)));
        check("a wrong password does not", false, () -> verify("correct horsf", store("correct horse", SALT, 1000)));
        check("a hash stored at another cost still verifies", true, () -> verify("old password", store("old password", SALT, 3000)));
        check("an unknown algorithm is rejected", false, () -> verify("correct horse", store("correct horse", SALT, 1000).replaceFirst("pbkdf2-sha256", "md5")));
        check("a non-numeric cost gives false", false, () -> verify("x", "pbkdf2-sha256$lots$c2FsdA==$aGFzaA=="));
        check("bad Base64 gives false", false, () -> verify("x", "pbkdf2-sha256$1000$not base64!$aGFzaA=="));
        check("zero iterations gives false", false, () -> verify("x", "pbkdf2-sha256$0$c2FsdA==$aGFzaA=="));
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
