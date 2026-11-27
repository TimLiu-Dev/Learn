package runner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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
        System.exit(code);   // after a SIGTERM this blocks; the hook returns and the JVM exits with 143
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
        var summaryPrinted = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Runs on SIGTERM and on every normal exit; requestStop() is false when there's nothing to drain.
            // The JVM halts as soon as this hook returns, so wait here until the summary is out.
            if (runner.requestStop()) {
                try {
                    summaryPrinted.await(opts.graceMs() + 10_000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }));

        Map<Outcome, Integer> counts = runner.run();
        events.summary("summary total=" + plan.size() + " " + Stream.of(Outcome.values())
            .map(o -> o.event() + "=" + counts.get(o)).collect(Collectors.joining(" ")));
        summaryPrinted.countDown();
        return counts.get(Outcome.DONE) == plan.size() ? 0 : 1;
    }
}
