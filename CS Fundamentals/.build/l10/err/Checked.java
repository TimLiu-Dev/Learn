import java.nio.file.Files;
import java.nio.file.Path;

public class Checked {
    static String loadConfig(String name) {
        return Files.readString(Path.of(name));   // readString declares: throws IOException
    }

    public static void main(String[] args) {
        System.out.println(loadConfig("app.conf"));
    }
}
