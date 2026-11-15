import java.util.List;
import java.util.StringJoiner;

public class Blank {
    public static void main(String[] args) {
        String crlf = "GET / HTTP/1.1\r\nHost: a\r\n\r\nbody";
        String lf   = "GET / HTTP/1.1\nHost: a\n\nbody";        // typed with plain \n
        StringJoiner out = new StringJoiner(" | ");
        for (String message : List.of(crlf, lf)) {
            String[] parts = message.split("\r\n\r\n", 2);       // head | body
            String[] headLines = parts[0].split("\r\n");
            out.add(parts.length + " " + headLines.length);
        }
        System.out.println(out);
    }
}
