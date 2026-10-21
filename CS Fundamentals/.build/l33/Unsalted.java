import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

public class Unsalted {
    static String sha256Hex(byte[] bytes) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)).substring(0, 16);
    }

    public static void main(String[] args) throws Exception {
        String[][] users = {{"ana", "sunshine1"}, {"ben", "sunshine1"}};

        System.out.println("unsalted SHA-256:");
        for (String[] u : users) System.out.println("  " + u[0] + " " + sha256Hex(u[1].getBytes()) + "…");

        System.out.println("salted (random 16-byte salt per user):");
        SecureRandom rng = new SecureRandom();
        for (String[] u : users) {
            byte[] salt = new byte[16];
            rng.nextBytes(salt);
            byte[] pw = u[1].getBytes();
            byte[] input = new byte[salt.length + pw.length];
            System.arraycopy(salt, 0, input, 0, salt.length);
            System.arraycopy(pw, 0, input, salt.length, pw.length);
            System.out.println("  " + u[0] + " " + sha256Hex(input) + "…");
        }
    }
}
