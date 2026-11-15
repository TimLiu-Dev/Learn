import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public class Practice {
    record Step(String name, boolean always, Callable<Integer> run) {
        Step(String name, Callable<Integer> run) { this(name, false, run); }
    }

    record JobResult(boolean passed, List<String> log) { }

    /** Runs the steps in order, like one CI job. */
    static JobResult runJob(List<Step> steps) {
        boolean passed = true;
        List<String> log = new ArrayList<>();
        for (Step step : steps) {
            if (!passed && !step.always()) {
                log.add("skipped " + step.name());
                continue;
            }
            // An exception is just another way for a step to fail (L10), not a reason to crash the runner.
            String problem;
            try {
                int code = step.run().call();
                problem = code == 0 ? null : "exit " + code;
            } catch (Exception e) {
                problem = "threw " + e.getClass().getSimpleName();
            }
            if (problem == null) {
                log.add("ok " + step.name());
            } else {
                log.add("FAILED " + step.name() + " (" + problem + ")");
                passed = false;
            }
        }
        return new JobResult(passed, log);
    }

    // CHECKS
    public static void main(String[] args) {
        check("a job whose steps all exit 0 passes", new JobResult(true, List.of("ok checkout", "ok build")),
                () -> runJob(List.of(new Step("checkout", () -> 0), new Step("build", () -> 0))));
        check("a non-zero exit fails the job and skips the rest", new JobResult(false, List.of("FAILED build (exit 1)", "skipped publish")),
                () -> runJob(List.of(new Step("build", () -> 1), new Step("publish", () -> 0))));
        boolean[] published = {false};
        check("skipped steps never run", false, () -> {
            runJob(List.of(new Step("build", () -> 2), new Step("publish", () -> { published[0] = true; return 0; })));
            return published[0];
        });
        check("an always step runs after a failure, and the job stays failed",
                new JobResult(false, List.of("FAILED test (exit 1)", "skipped lint", "ok upload report")),
                () -> runJob(List.of(new Step("test", () -> 1), new Step("lint", () -> 0), new Step("upload report", true, () -> 0))));
        check("a step that throws is a failure, not a crash",
                new JobResult(false, List.of("FAILED build (threw IllegalStateException)", "skipped test")),
                () -> runJob(List.of(new Step("build", () -> { throw new IllegalStateException("disk full"); }), new Step("test", () -> 0))));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
