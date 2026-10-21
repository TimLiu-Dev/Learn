import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Hashes {
    static byte[] sha256(String s) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(s.getBytes());
    }

    static int bitsDiffer(byte[] a, byte[] b) {
        int n = 0;
        for (int i = 0; i < a.length; i++) n += Integer.bitCount((a[i] ^ b[i]) & 0xff);
        return n;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        byte[] h1 = sha256("hello");
        byte[] h2 = sha256("hellp");
        HexFormat hex = HexFormat.of();
        System.out.println("hello -> " + hex.formatHex(h1));
        System.out.println("hellp -> " + hex.formatHex(h2));
        System.out.println("bits that differ: " + bitsDiffer(h1, h2) + " of " + h1.length * 8);
        // L19's hashCode is built for speed, not security
        System.out.println("\"Aa\".hashCode() = " + "Aa".hashCode() + ", \"BB\".hashCode() = " + "BB".hashCode());
    }
}
