package runner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Main {
    private Main() {}

    public static void main(String[] args) throws InterruptedException {
        int code;
        try {
            code = run(args);
        } catch (InputError e) {
            System.err.println("error: " + e.getMessage());
            code = 2;
        }
        System.exit(code);
    }

    private static int run(String[] args) throws InputError, InterruptedException {
        Options opts = Options.parse(args);
        String text;
        try {
            text = Files.readString(Path.of(opts.file()));
        } catch (IOException e) {
            throw new InputError("cannot read " + opts.file() + ": " + e.getMessage());
        }
        List<Job> plan = Plan.order(JobsFile.parse(text));

        var events = new Events(System.out);
        var runner = new Runner(plan, opts, events);
        // Milestone 5: a shutdown hook (L53) that drains before the JVM halts.

        Map<Outcome, Integer> counts = runner.run();
        events.summary("summary total=" + plan.size() + " " + Stream.of(Outcome.values())
            .map(o -> o.event() + "=" + counts.get(o)).collect(Collectors.joining(" ")));
        return counts.get(Outcome.DONE) == plan.size() ? 0 : 1;
    }
}
