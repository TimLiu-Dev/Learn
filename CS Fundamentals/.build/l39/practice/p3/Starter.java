import java.util.*;

public class Practice {
    interface Log {
        void write(String line);
        List<String> lines();
    }

    static final class MemoryLog implements Log {
        private final List<String> stored = new ArrayList<>();
        public void write(String line) { stored.add(line); }
        public List<String> lines() { return List.copyOf(stored); }
    }

    // Java has no "by": each decorator forwards lines() by hand
    static final class Numbered implements Log {
        private final Log inner;
        Numbered(Log inner) { this.inner = inner; }
        public void write(String line) { throw new UnsupportedOperationException("TODO: number the line, then pass it on"); }
        public List<String> lines() { return inner.lines(); }
    }

    static final class Shortened implements Log {
        private final Log inner;
        private final int max;
        Shortened(Log inner, int max) { this.inner = inner; this.max = max; }
        public void write(String line) { throw new UnsupportedOperationException("TODO: cut the line to max characters, then pass it on"); }
        public List<String> lines() { return inner.lines(); }
    }

    static final class SkipBlank implements Log {
        private final Log inner;
        SkipBlank(Log inner) { this.inner = inner; }
        public void write(String line) { throw new UnsupportedOperationException("TODO: pass on only lines with something in them"); }
        public List<String> lines() { return inner.lines(); }
    }

    // CHECKS
    // A decorator your code has never seen
    static final class Shouting implements Log {
        private final Log inner;
        Shouting(Log inner) { this.inner = inner; }
        public void write(String line) { inner.write(line.toUpperCase()); }
        public List<String> lines() { return inner.lines(); }
    }

    static List<String> writeAll(Log log, String... lines) {
        for (String l : lines) log.write(l);
        return log.lines();
    }

    public static void main(String[] args) {
        check("numbered", List.of("1: a", "2: b"), () -> writeAll(new Numbered(new MemoryLog()), "a", "b"));
        check("shortened", List.of("hello", "hi"), () -> writeAll(new Shortened(new MemoryLog(), 5), "hello world", "hi"));
        check("skip blank", List.of("a", "b"), () -> writeAll(new SkipBlank(new MemoryLog()), "a", "", "   ", "b"));
        check("numbering outside the filter means nothing is blank any more", List.of("1: a", "2: ", "3: b"),
            () -> writeAll(new Numbered(new SkipBlank(new MemoryLog())), "a", "", "b"));
        check("numbering inside the filter counts only what is kept", List.of("1: a", "2: b"),
            () -> writeAll(new SkipBlank(new Numbered(new MemoryLog())), "a", "", "b"));
        check("shortening outside vs inside the numbers", "[1: abc] [1: ]",
            () -> writeAll(new Shortened(new Numbered(new MemoryLog()), 3), "abcdef") + " " + writeAll(new Numbered(new Shortened(new MemoryLog(), 3)), "abcdef"));
        check("a new decorator stacks with yours", List.of("1: DUNE", "2: EMMA"),
            () -> writeAll(new Shouting(new SkipBlank(new Numbered(new MemoryLog()))), "dune", " ", "emma"));
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
