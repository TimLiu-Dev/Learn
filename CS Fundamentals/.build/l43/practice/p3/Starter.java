import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Practice {
    // One span of a trace: start and end are ms since the trace began. parent is null for the root.
    record Span(String id, String parent, String service, long start, long end) { }

    // Span id -> its duration minus the time covered by its children (overlaps counted once).
    static Map<String, Long> selfTimes(List<Span> spans) {
        throw new UnsupportedOperationException("TODO: span id -> self time in ms");
    }

    // Where to look first: the service of the span with the largest self time.
    static String culprit(List<Span> spans) {
        throw new UnsupportedOperationException("TODO: the service with the largest self time");
    }

    // CHECKS
    public static void main(String[] args) {
        List<Span> chain = List.of(
            new Span("p", "b", "pricing", 19, 150),
            new Span("g", null, "gateway", 0, 176),
            new Span("b", "g", "books", 3, 175));
        check("a leaf's self time is its whole duration", 131L, () -> selfTimes(chain).get("p"));
        check("a chain gives the lesson's waterfall", Map.of("g", 4L, "b", 41L, "p", 131L), () -> selfTimes(chain));
        check("parallel children are counted once", 40L, () -> selfTimes(List.of(
            new Span("g", null, "gateway", 0, 100),
            new Span("a", "g", "stock", 10, 60),
            new Span("r", "g", "reviews", 20, 70))).get("g"));
        check("overlaps and gaps in any order", 30L, () -> selfTimes(List.of(
            new Span("c", "g", "search", 60, 90),
            new Span("a", "g", "stock", 10, 40),
            new Span("g", null, "gateway", 0, 100),
            new Span("b", "g", "reviews", 30, 50))).get("g"));
        check("the culprit is the service that spent the time itself", "pricing", () -> culprit(chain));
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
