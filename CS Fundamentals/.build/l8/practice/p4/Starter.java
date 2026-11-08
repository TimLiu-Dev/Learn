import java.util.*;

public class Practice {
    // Java has no "by": this wrapper holds a set and forwards just the methods it needs.
    static final class InstrumentedSet<E> {
        private final Set<E> inner;
        private int addCount = 0;

        InstrumentedSet(Set<E> inner) { this.inner = inner; }

        boolean add(E e) { throw new UnsupportedOperationException("TODO: count the attempt, then forward"); }
        boolean addAll(Collection<? extends E> c) { throw new UnsupportedOperationException("TODO: count every element, then forward"); }
        int size() { return inner.size(); }
        List<E> toList() { return new ArrayList<>(inner); }
        int addCount() { return addCount; }
    }

    // CHECKS
    public static void main(String[] args) {
        check("addAll counts each element once", 3, () -> { var s = new InstrumentedSet<String>(new HashSet<>()); s.addAll(List.of("a", "b", "c")); return s.addCount(); });
        check("duplicates count as attempts", 4, () -> { var s = new InstrumentedSet<String>(new HashSet<>()); s.add("a"); s.addAll(List.of("a", "b")); s.add("b"); return s.addCount(); });
        check("duplicates are stored once", 2, () -> { var s = new InstrumentedSet<String>(new HashSet<>()); s.add("a"); s.addAll(List.of("a", "b")); s.add("b"); return s.size(); });
        check("any set can be wrapped", List.of("c", "a", "b"), () -> { var s = new InstrumentedSet<String>(new LinkedHashSet<>()); s.addAll(List.of("c", "a", "b")); return s.toList(); });
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
