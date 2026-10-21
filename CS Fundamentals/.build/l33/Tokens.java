import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class Tokens {
    public static void main(String[] args) {
        // Seeded Random: the same "random" numbers every run (handy in L18, fatal here)
        long a = new Random(42).nextLong();
        long b = new Random(42).nextLong();
        System.out.println("Random(42) twice: " + a + ", " + b + ", same=" + (a == b));

        // A session token: 32 bytes from the OS's cryptographic source
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        System.out.println("token length: " + token.length() + " chars");
    }
}
