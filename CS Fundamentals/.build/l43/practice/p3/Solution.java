import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Practice {
    // One span of a trace: start and end are ms since the trace began. parent is null for the root.
    record Span(String id, String parent, String service, long start, long end) { }

    // Total length of some spans' intervals, counting overlaps once: sort by start, then sweep.
    static long covered(List<Span> children) {
        List<Span> sorted = new ArrayList<>(children);
        sorted.sort(Comparator.comparingLong(Span::start));
        long total = 0, reach = Long.MIN_VALUE;            // reach: the furthest end seen so far
        for (Span c : sorted) {
            long from = Math.max(c.start(), reach);         // skip the part already counted
            if (c.end() > from) total += c.end() - from;
            reach = Math.max(reach, c.end());
        }
        return total;
    }

    // Span id -> its duration minus the time covered by its children (overlaps counted once).
    static Map<String, Long> selfTimes(List<Span> spans) {
        Map<String, List<Span>> children = new HashMap<>();
        for (Span s : spans) {
            if (s.parent() != null) children.computeIfAbsent(s.parent(), k -> new ArrayList<>()).add(s);
        }
        Map<String, Long> self = new HashMap<>();
        for (Span s : spans) {
            self.put(s.id(), (s.end() - s.start()) - covered(children.getOrDefault(s.id(), List.of())));
        }
        return self;
    }

    // Where to look first: the service of the span with the largest self time.
    static String culprit(List<Span> spans) {
        Map<String, Long> self = selfTimes(spans);
        return spans.stream().max(Comparator.comparingLong(s -> self.get(s.id()))).orElseThrow().service();
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
