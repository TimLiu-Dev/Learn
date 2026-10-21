import java.security.MessageDigest;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SpeedGap {
    public static void main(String[] args) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] pw = "sunshine1".getBytes();
        int sink = 0;
        for (int i = 0; i < 200_000; i++) sink += md.digest(pw)[0];  // warm-up
        int n = 1_000_000;
        long t = System.nanoTime();
        for (int i = 0; i < n; i++) sink += md.digest(pw)[0];
        long shaNs = (System.nanoTime() - t) / n;

        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] salt = new byte[16];
        f.generateSecret(new PBEKeySpec("w".toCharArray(), salt, 600_000, 256));  // warm-up
        t = System.nanoTime();
        for (int i = 0; i < 5; i++)
            sink += f.generateSecret(new PBEKeySpec("sunshine1".toCharArray(), salt, 600_000, 256)).getEncoded()[0];
        long pbNs = (System.nanoTime() - t) / 5;

        System.out.println("SHA-256, one guess:   " + shaNs + " ns  (~" + 1_000_000_000L / shaNs + " guesses/s on one core)");
        System.out.println("PBKDF2 600k, one guess: " + pbNs / 1_000_000 + " ms  (~" + 1_000_000_000L / pbNs + " guesses/s)");
        System.out.println("slowdown: ~" + pbNs / shaNs + "x   (sink " + sink + ")");
    }
}
