import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Token {
    public static void main(String[] args) {
        String expected = "tok123";                          // stored in ana's session
        String header = "X-CSRF-Token: tok123";              // ana's own page sent the right token
        String presented = header.substring(header.indexOf(':') + 2);

        boolean same = presented == expected;
        boolean equal = presented.equals(expected);
        boolean bytes = MessageDigest.isEqual(
                presented.getBytes(StandardCharsets.UTF_8), expected.getBytes(StandardCharsets.UTF_8));
        System.out.println(same + " " + equal + " " + bytes);
    }
}
