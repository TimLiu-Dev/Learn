import java.nio.file.*;

public class ReadConfig {
    public static void main(String[] args) {
        String text = Files.readString(Path.of("config.txt"));
        System.out.println(text);
    }
}
