import java.util.Base64;

public class Encode {
    public static void main(String[] args) {
        String secret = "password123";
        String encoded = Base64.getEncoder().encodeToString(secret.getBytes());
        System.out.println("encoded: " + encoded);
        // Anyone can reverse it: no key involved
        String decoded = new String(Base64.getDecoder().decode(encoded));
        System.out.println("decoded: " + decoded);
    }
}
