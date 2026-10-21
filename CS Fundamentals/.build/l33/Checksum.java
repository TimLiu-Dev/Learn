import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Checksum {
    // Streams the file in 8 KB chunks, so a 4 GB file never sits in memory (L22)
    static String sha256File(String path) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream in = new FileInputStream(path)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) >= 0) md.update(buf, 0, n);
        }
        return HexFormat.of().formatHex(md.digest());
    }

    public static void main(String[] args) throws Exception {
        String actual = sha256File(args[0]);
        System.out.println("actual:   " + actual);
        System.out.println(actual.equals(args[1]) ? "OK: matches" : "MISMATCH: do not trust this file");
    }
}
