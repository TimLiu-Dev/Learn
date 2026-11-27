package runner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

/** Runs the real Runner on real threads, with short jobs, and checks the rules on its output. */
class RunnerTest {
    private record Result(Map<Outcome, Integer> counts, List<String> lines) {}

    private static Result run(String text, Options opts) throws Exception {
        var buffer = new ByteArrayOutputStream();
        var counts = new Runner(Plan.order(JobsFile.parse(text)), opts, new Events(new PrintStream(buffer, true))).run();
        return new Result(counts, buffer.toString().lines().toList());
    }

    /** The text after the timestamp, e.g. "start a attempt=1". */
    private static List<String> events(Result r) {
        return r.lines().stream().map(l -> l.substring(l.indexOf(' ') + 1)).filter(l -> !l.startsWith("queued")).toList();
    }

    private static long time(Result r, String event) {
        String line = r.lines().stream().filter(l -> l.endsWith(" " + event)).findFirst().orElseThrow();
        return Long.parseLong(line.substring(2, line.indexOf(' ')));
    }

    @Test
    void retriesWithBackoffAndSucceedsOnTheThirdAttempt() throws Exception {
        Result r = run("id=a duration=20 fail=2", new Options("f", 1, 3, 0, 16, 3000));
        assertEquals(List.of("start a attempt=1", "fail a attempt=1", "retry a attempt=2 delay=50", "start a attempt=2",
            "fail a attempt=2", "retry a attempt=3 delay=100", "start a attempt=3", "done a attempt=3"), events(r));
        assertTrue(time(r, "start a attempt=2") - time(r, "fail a attempt=1") >= 50);
        assertTrue(time(r, "start a attempt=3") - time(r, "fail a attempt=2") >= 100);
    }

    @Test
    void aTimeoutCancelsTheWorkAndTheRunEndsQuickly() throws Exception {
        long t0 = System.nanoTime();
        Result r = run("id=slow duration=5000", new Options("f", 1, 0, 100, 16, 3000));
        long ms = (System.nanoTime() - t0) / 1_000_000;
        assertEquals(List.of("start slow attempt=1", "timeout slow attempt=1", "giveup slow attempts=1"), events(r));
        assertTrue(ms < 2000, "run() returned after " + ms + " ms: the timed-out work kept the executor busy");
    }

    @Test
    void neverRunsMoreThanWorkersAttemptsAtOnce() throws Exception {
        String text = IntStream.rangeClosed(1, 12).mapToObj(i -> "id=j" + i + " duration=30").collect(Collectors.joining("\n"));
        Result r = run(text, new Options("f", 3, 0, 0, 2, 3000));
        int running = 0, peak = 0;
        for (String e : events(r)) {
            if (e.startsWith("start")) running++;
            if (e.startsWith("done")) running--;
            peak = Math.max(peak, running);
        }
        assertEquals(3, peak);
        assertEquals(12, r.counts().get(Outcome.DONE));
    }

    @Test
    void countsAddUpUnderLoad() throws Exception {
        String text = IntStream.rangeClosed(1, 2000).mapToObj(i -> "id=j" + i + " duration=0").collect(Collectors.joining("\n"));
        Result r = run(text, new Options("f", 64, 0, 0, 4, 3000));
        assertEquals(2000, r.counts().get(Outcome.DONE));
        assertEquals(2000, events(r).stream().filter(e -> e.startsWith("done")).count());
    }

    @Test
    void skipsEverythingDownstreamOfAJobThatGivesUp() throws Exception {
        Result r = run("id=a duration=10 fail=9\nid=b duration=10 deps=a\nid=c duration=10 deps=b\nid=d duration=10",
            new Options("f", 2, 0, 0, 16, 3000));
        assertTrue(events(r).contains("skip b dep=a"), r.lines().toString());
        assertTrue(events(r).contains("skip c dep=b"), r.lines().toString());
        assertEquals(Map.of(Outcome.DONE, 1, Outcome.GIVEUP, 1, Outcome.SKIP, 2, Outcome.CANCEL, 0, Outcome.DROP, 0), r.counts());
    }
}
