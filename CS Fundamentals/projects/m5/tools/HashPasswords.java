import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Makes a users file for the chat server from plain "name:password" lines.
 *
 *   java tools/HashPasswords.java [--iterations N] < data/users.plain.txt > users.txt
 *
 * Each output line is name:iterations:saltBase64:hashBase64, with a fresh 16-byte salt per user and
 * PBKDF2-HMAC-SHA256 producing 32 bytes (L33). The default, 600,000 iterations, is OWASP's
 * recommendation; tests may use fewer to stay fast, because the count is stored in each line.
 * Lines that are blank or start with # are copied unchanged.
 */
public class HashPasswords {
    public static void main(String[] args) throws Exception {
        int iterations = 600_000;
        if (args.length == 2 && args[0].equals("--iterations")) iterations = Integer.parseInt(args[1]);
        else if (args.length != 0) {
            System.err.println("usage: java HashPasswords.java [--iterations N] < plain.txt > users.txt");
            System.exit(2);
        }
        var rng = new SecureRandom();
        var b64 = Base64.getEncoder();
        var in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String line;
        int lineNo = 0;
        while ((line = in.readLine()) != null) {
            lineNo++;
            if (line.isBlank() || line.startsWith("#")) { System.out.println(line); continue; }
            int colon = line.indexOf(':');
            String name = colon < 0 ? "" : line.substring(0, colon);
            if (!name.matches("[a-z0-9_]{1,16}")) {
                System.err.println("error: line " + lineNo + ": expected name:password with name [a-z0-9_]{1,16}");
                System.exit(2);
            }
            byte[] salt = new byte[16];
            rng.nextBytes(salt);
            var spec = new PBEKeySpec(line.substring(colon + 1).toCharArray(), salt, iterations, 256);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            System.out.println(name + ":" + iterations + ":" + b64.encodeToString(salt) + ":" + b64.encodeToString(hash));
        }
    }
}
