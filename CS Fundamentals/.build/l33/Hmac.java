import java.security.MessageDigest;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Hmac {
    static byte[] hmac(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(msg.getBytes());
    }

    public static void main(String[] args) throws Exception {
        byte[] key = "server-side-secret".getBytes();  // in real code: from an env var
        String msg = "user=42&role=member";
        byte[] tag = hmac(key, msg);
        System.out.println("tag: " + HexFormat.of().formatHex(tag).substring(0, 16) + "…");

        // The server recomputes the tag and compares in constant time
        System.out.println("genuine: " + MessageDigest.isEqual(tag, hmac(key, msg)));
        System.out.println("edited:  " + MessageDigest.isEqual(tag, hmac(key, "user=42&role=admin")));
    }
}
