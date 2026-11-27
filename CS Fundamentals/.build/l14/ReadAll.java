import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.List;

public class ReadAll {
    // Function.apply declares no checked exceptions, so the IOException must be wrapped.
    static String read(Path p) {
        try {
            return Files.readString(p);
        } catch (IOException e) {
            throw new UncheckedIOException(e);    // unchecked, and the IOException is kept as the cause
        }
    }

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("l14");
        Path a = Files.writeString(dir.resolve("a.txt"), "alpha");
        Path b = Files.writeString(dir.resolve("b.txt"), "beta");
        System.out.println(List.of(a, b).stream().map(ReadAll::read).toList());
        try {
            List.of(a, dir.resolve("missing.txt")).stream().map(ReadAll::read).toList();
        } catch (UncheckedIOException e) {
            System.out.println("caught UncheckedIOException, cause " + e.getCause().getClass().getSimpleName());
        }
    }
}
