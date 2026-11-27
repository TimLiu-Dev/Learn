package budget;

import java.util.List;

/** What running a command produced. Main turns it into streams and an exit code. */
public sealed interface Outcome {
    record Done(List<String> lines, List<String> warnings, int exitCode) implements Outcome {
        Done(List<String> lines) {
            this(lines, List.of(), 0);
        }
    }

    record Failed(String message) implements Outcome {}
}
