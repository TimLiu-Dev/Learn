import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

// The same pipeline started from Java, with no shell in between
public class Pipe {
    public static void main(String[] args) throws IOException, InterruptedException {
        List<ProcessBuilder> stages = List.of(
            new ProcessBuilder("cut", "-d", " ", "-f3", "requests.log"),
            new ProcessBuilder("sort"),
            new ProcessBuilder("uniq", "-c"),
            new ProcessBuilder("sort", "-rn"),
            new ProcessBuilder("head", "-5"));
        List<Process> procs = ProcessBuilder.startPipeline(stages); // stdout of each one feeds stdin of the next
        try (var reader = procs.getLast().inputReader()) {
            reader.lines().forEach(System.out::println);
        }
        for (int i = 0; i < stages.size(); i++) {
            String shown = stages.get(i).command().stream()
                .map(a -> a.contains(" ") ? "'" + a + "'" : a)
                .collect(Collectors.joining(" "));
            System.out.println("exit " + procs.get(i).waitFor() + "  " + shown);
        }
    }
}
