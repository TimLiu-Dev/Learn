import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Checked {
    // Option 1: declare it and let the caller decide
    static String loadConfig(String name) throws IOException {
        return Files.readString(Path.of(name));
    }

    public static void main(String[] args) {
        // Option 2: catch it where you can actually do something about it
        try {
            System.out.println(loadConfig("app.conf"));
        } catch (IOException e) {
            System.out.println("using defaults, because: " + e);
        }
    }
}
