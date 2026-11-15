import java.util.*;

public class Practice {
    // The heap: each object's name -> the names it refers to. Returns null when nothing reaches target.
    static List<String> leakPath(String target, List<String> roots, Map<String, List<String>> heap) {
        throw new UnsupportedOperationException("TODO: BFS from all the roots, remember parents, walk back from target");
    }

    // CHECKS
    // Leak 2 from the lesson: two screens, both closed, neither unsubscribed.
    static final Map<String, List<String>> BUS = Map.of(
        "BUS", List.of("listeners"),
        "listeners", List.of("lambda#1", "lambda#2"),
        "lambda#1", List.of("Screen#1"),
        "lambda#2", List.of("Screen#2"),
        "Screen#1", List.of("bitmap#1"),
        "Screen#2", List.of("bitmap#2"),
        "bitmap#1", List.of(),
        "bitmap#2", List.of(),
        "old frame", List.of("bitmap#1"));

    static final Map<String, List<String>> APP = Map.of(
        "App", List.of("cache", "sessions"),
        "sessions", List.of("table"),
        "table", List.of("node"),
        "node", List.of("Session#7"),
        "cache", List.of("Session#7"),
        "Session#7", List.of());

    static final Map<String, List<String>> CYCLES = Map.of(
        "R", List.of("X"), "X", List.of("Y"), "Y", List.of("X", "T"), "T", List.of(),
        "P", List.of("Q"), "Q", List.of("P", "U"), "U", List.of());

    public static void main(String[] args) {
        check("the forgotten listener, from the bus to the bitmap", List.of("BUS", "listeners", "lambda#2", "Screen#2", "bitmap#2"), () -> leakPath("bitmap#2", List.of("BUS"), BUS));
        check("an object nothing reaches has no trace", null, () -> leakPath("old frame", List.of("BUS"), BUS));
        check("a root's own object is a chain of one", List.of("BUS"), () -> leakPath("BUS", List.of("BUS"), BUS));
        check("the shortest chain wins", List.of("App", "cache", "Session#7"), () -> leakPath("Session#7", List.of("App"), APP));
        check("cycles don't trap the search", "[R, X, Y, T] null", () -> leakPath("T", List.of("R"), CYCLES) + " " + leakPath("U", List.of("R"), CYCLES));
        check("any root can start the chain", List.of("Screen#1", "bitmap#1"), () -> leakPath("bitmap#1", List.of("BUS", "Screen#1"), BUS));
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
