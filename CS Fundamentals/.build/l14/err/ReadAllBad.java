import java.nio.file.*;
import java.util.List;

public class ReadAllBad {
    public static void main(String[] args) {
        List<Path> paths = List.of(Path.of("a.txt"), Path.of("b.txt"));
        List<String> texts = paths.stream().map(Files::readString).toList();
        System.out.println(texts);
    }
}
