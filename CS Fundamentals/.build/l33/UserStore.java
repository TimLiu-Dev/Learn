import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserStore {
    private final int iterations;
    private final Map<String, String> users = new HashMap<>();  // name -> stored hash string

    UserStore(int iterations) { this.iterations = iterations; }

    private static byte[] pbkdf2(String pw, byte[] salt, int iter) throws Exception {
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(new PBEKeySpec(pw.toCharArray(), salt, iter, 256)).getEncoded();
    }

    private static String hash(String pw, int iter) throws Exception {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        Base64.Encoder b64 = Base64.getEncoder();
        return "pbkdf2-sha256$" + iter + "$" + b64.encodeToString(salt) + "$" + b64.encodeToString(pbkdf2(pw, salt, iter));
    }

    void register(String name, String pw, int iter) throws Exception {
        if (users.containsKey(name)) throw new IllegalArgumentException("name taken: " + name);
        if (pw.length() < 8) throw new IllegalArgumentException("password too short");
        users.put(name, hash(pw, iter));
    }

    void register(String name, String pw) throws Exception { register(name, pw, iterations); }

    boolean login(String name, String pw) throws Exception {
        String stored = users.get(name);
        if (stored == null) return false;
        String[] p = stored.split("\\$");
        Base64.Decoder unb64 = Base64.getDecoder();
        int iter = Integer.parseInt(p[1]);
        boolean ok = MessageDigest.isEqual(pbkdf2(pw, unb64.decode(p[2]), iter), unb64.decode(p[3]));
        if (ok && iter < iterations) {
            users.put(name, hash(pw, iterations));  // upgrade on the next successful login
            System.out.println("  upgraded " + name + " from " + iter + " to " + iterations + " iterations");
        }
        return ok;
    }

    String costOf(String name) { return users.get(name).split("\\$")[1]; }

    public static void main(String[] args) throws Exception {
        UserStore store = new UserStore(600_000);
        store.register("ana", "correct horse");
        store.register("old", "legacy pass", 100_000);  // created years ago
        System.out.println("ana right: " + store.login("ana", "correct horse"));
        System.out.println("ana wrong: " + store.login("ana", "correct hors"));
        System.out.println("ghost:     " + store.login("ghost", "anything at all"));
        System.out.println("old cost before: " + store.costOf("old"));
        System.out.println("old login: " + store.login("old", "legacy pass"));
        System.out.println("old cost after:  " + store.costOf("old"));
        try { store.register("ana", "another pass"); } catch (IllegalArgumentException e) { System.out.println("register: " + e.getMessage()); }
    }
}
