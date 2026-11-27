import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class Unwrap {
    static int parse(String line) {
        try {
            if (line.isEmpty()) throw new IOException("empty line");
            return line.length();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(List.of("ab", "", "cde").stream().map(Unwrap::parse).toList());
        } catch (UncheckedIOException e) {
            System.out.println(e.getMessage());
        }
    }
}
