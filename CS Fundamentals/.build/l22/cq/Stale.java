import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Stale {
    public static void main(String[] args) throws IOException {
        var in = new ByteArrayInputStream("abcdefghij".getBytes());
        var out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer);                        // not write(buffer, 0, n)
        }
        System.out.println(out);
    }
}
