import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class Practice {
    /** Holds on books, first come, first served. No operation throws. */
    static class Holds {
        private final Map<String, List<String>> lines = new HashMap<>();   // title -> members, in order

        /** Puts member in line for title (once) and returns their 1-based place. */
        public int place(String title, String member) {
            List<String> line = lines.computeIfAbsent(title, t -> new ArrayList<>());
            if (!line.contains(member)) line.add(member);
            return line.indexOf(member) + 1;
        }

        /** Removes and returns the first member waiting for title, or null if nobody is. */
        public String next(String title) {
            List<String> line = lines.get(title);
            return line == null || line.isEmpty() ? null : line.remove(0);
        }

        public void cancel(String title, String member) {
            List<String> line = lines.get(title);
            if (line != null) line.remove(member);
        }

        /** Who's waiting for title, in order, as an unmodifiable copy. */
        public List<String> waiting(String title) {
            List<String> line = lines.get(title);
            return line == null ? List.of() : List.copyOf(line);
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("first come, first served", List.of(1, 2, List.of("Ada", "Linus"), "Ada", List.of("Linus")), () -> {
            Holds h = new Holds();
            return List.of(h.place("Dune", "Ada"), h.place("Dune", "Linus"), h.waiting("Dune"), h.next("Dune"), h.waiting("Dune"));
        });
        check("placing twice keeps your first place", List.of(1, List.of("Ada", "Linus")), () -> {
            Holds h = new Holds();
            h.place("Dune", "Ada");
            h.place("Dune", "Linus");
            return List.of(h.place("Dune", "Ada"), h.waiting("Dune"));
        });
        check("next and cancel have no error cases", "null null []", () -> {
            Holds h = new Holds();
            String nobody = h.next("Emma");
            h.cancel("Emma", "Ada");
            h.place("Dune", "Ada");
            h.cancel("Dune", "Ada");
            return nobody + " " + h.next("Dune") + " " + h.waiting("Dune");
        });
        check("waiting can't be used to change the queue", List.of("Ada"), () -> {
            Holds h = new Holds();
            h.place("Dune", "Ada");
            try { h.waiting("Dune").add("Eve"); } catch (UnsupportedOperationException e) { /* refused: good */ }
            return h.waiting("Dune");
        });
        check("only the four operations are public", Set.of("place", "next", "cancel", "waiting"), () ->
                Arrays.stream(Holds.class.getDeclaredMethods())
                        .filter(m -> Modifier.isPublic(m.getModifiers()) && !m.isSynthetic())
                        .map(Method::getName).collect(Collectors.toSet()));
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
