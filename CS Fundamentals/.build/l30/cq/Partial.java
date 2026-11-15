import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;

public class Partial {
    public static void main(String[] args) throws Exception {
        // Stands in for a socket: "hel" has arrived, "lo" is still on its way.
        InputStream in = new SequenceInputStream(
            new ByteArrayInputStream("hel".getBytes()),
            new ByteArrayInputStream("lo".getBytes()));
        byte[] buf = new byte[5];
        int n = in.read(buf);
        System.out.println("read " + n + ": " + new String(buf, 0, n));
    }
}
