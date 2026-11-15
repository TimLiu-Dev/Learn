import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class Practice {
    // grep -c: how many lines matched, and the exit status
    record GrepResult(int count, int status) { }

    // How a process ended
    sealed interface Ended permits Exited, Signalled { }
    record Exited(int code) implements Ended { }
    record Signalled(String signal) implements Ended { }

    static final Map<String, Integer> SIGNALS = Map.of("HUP", 1, "INT", 2, "KILL", 9, "PIPE", 13, "TERM", 15);

    static GrepResult grepC(String pattern, List<String> lines) {
        Pattern p = Pattern.compile(pattern);
        int count = 0;
        for (String line : lines) if (p.matcher(line).find()) count++;   // find(): anywhere in the line
        return new GrepResult(count, count > 0 ? 0 : 1);
    }

    // What $? shows
    static int status(Ended ended) {
        if (ended instanceof Exited e) return e.code();
        if (ended instanceof Signalled s) return 128 + SIGNALS.get(s.signal());
        throw new IllegalStateException("unreachable: Ended is sealed");
    }

    // CHECKS
    public static void main(String[] args) {
        List<String> log = List.of(
            "09:00:01 GET /books 200 12ms",
            "09:00:02 GET /books/7 500 31ms",
            "09:00:03 POST /books 201 500ms",
            "09:00:04 GET /health 500 2ms");
        check("grep -c counts the lines that contain the pattern", new GrepResult(2, 0), () -> grepC(" 500 ", log));
        check("without the spaces, 500 also matches a latency", new GrepResult(3, 0), () -> grepC("500", log));
        check("no match prints 0 and exits 1", new GrepResult(0, 1), () -> grepC(" 404 ", log));
        check("an exit code passes straight through", 2, () -> status(new Exited(2)));
        check("SIGTERM gives 143", 143, () -> status(new Signalled("TERM")));
        check("SIGKILL gives 137", 137, () -> status(new Signalled("KILL")));
        check("SIGPIPE gives 141", 141, () -> status(new Signalled("PIPE")));
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
