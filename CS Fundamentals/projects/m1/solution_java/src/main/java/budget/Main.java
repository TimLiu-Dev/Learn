package budget;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** The thin IO shell: the only class that reads files, prints or exits. */
public final class Main {
    private Main() {}

    public static void main(String[] args) {
        Outcome outcome = switch (Cli.parse(List.of(args))) {
            case Parsed.Ok<Command>(Command command) -> Execute.run(command, Main::readLinesOrNull);
            case Parsed.Bad<Command>(String reason) -> new Outcome.Failed(reason);
        };
        switch (outcome) {
            case Outcome.Done(List<String> lines, List<String> warnings, int exitCode) -> {
                lines.forEach(System.out::println);
                warnings.forEach(System.err::println);
                System.exit(exitCode);
            }
            case Outcome.Failed(String message) -> {
                System.err.println("error: " + message);
                System.exit(1);
            }
        }
    }

    /** Files.readAllLines reads UTF-8 and splits on \n, \r\n and \r. L22 explains files properly. */
    private static List<String> readLinesOrNull(String path) {
        try {
            return Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            return null;
        }
    }
}
