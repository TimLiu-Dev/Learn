import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Practice {
    // rings: module -> ring number (0 = the domain, bigger = further out)
    // deps:  module -> the modules it depends on
    static List<String> violations(Map<String, Integer> rings, Map<String, Set<String>> deps) {
        List<String> found = new ArrayList<>();
        deps.forEach((from, tos) -> {
            for (String to : tos) if (rings.get(to) > rings.get(from)) found.add(from + " -> " + to);
        });
        found.sort(null);
        return found;
    }

    static List<String> findCycle(Map<String, Set<String>> deps) {
        Set<String> done = new HashSet<>();                 // fully explored: no cycle through here
        for (String start : deps.keySet()) {
            List<String> cycle = visit(start, deps, new ArrayList<>(), done);
            if (cycle != null) return cycle;
        }
        return null;
    }

    private static List<String> visit(String m, Map<String, Set<String>> deps, List<String> path, Set<String> done) {
        if (path.contains(m)) {                             // back on our own path: a cycle
            List<String> cycle = new ArrayList<>(path.subList(path.indexOf(m), path.size()));
            cycle.add(m);
            return cycle;
        }
        if (done.contains(m)) return null;
        path.add(m);
        for (String next : deps.getOrDefault(m, Set.of())) {
            List<String> cycle = visit(next, deps, path, done);
            if (cycle != null) return cycle;
        }
        path.remove(path.size() - 1);
        done.add(m);
        return null;
    }

    // CHECKS
    static final Map<String, Integer> RINGS = Map.of("domain", 0, "app", 1, "sqlite", 2, "web", 2, "main", 3);

    static Map<String, Set<String>> hexagonal() {
        Map<String, Set<String>> deps = new TreeMap<>();
        deps.put("domain", Set.of());
        deps.put("app", Set.of("domain"));
        deps.put("sqlite", Set.of("domain"));
        deps.put("web", Set.of("app", "domain"));
        deps.put("main", Set.of("app", "sqlite", "web", "domain"));
        return deps;
    }

    static String describeCycle(Map<String, Set<String>> deps, List<String> cycle) {
        if (cycle == null) return "null";
        if (cycle.size() < 2 || !cycle.get(0).equals(cycle.get(cycle.size() - 1))) return "not a loop: " + cycle;
        for (int i = 0; i + 1 < cycle.size(); i++) {
            if (!deps.getOrDefault(cycle.get(i), Set.of()).contains(cycle.get(i + 1))) return "not edges: " + cycle;
        }
        return "cycle through " + new TreeSet<>(cycle);
    }

    public static void main(String[] args) {
        check("dependencies that point inward break no rule", List.of(), () -> violations(RINGS, hexagonal()));
        check("the domain reaching out to the database is caught", List.of("domain -> sqlite"), () -> {
            Map<String, Set<String>> deps = hexagonal();
            deps.put("domain", Set.of("sqlite"));
            return violations(RINGS, deps);
        });
        check("every outward arrow is listed, sorted", List.of("app -> sqlite", "app -> web", "sqlite -> main"), () -> {
            Map<String, Set<String>> deps = hexagonal();
            deps.put("app", Set.of("domain", "web", "sqlite"));
            deps.put("sqlite", Set.of("main"));
            return violations(RINGS, deps);
        });
        check("a clean graph has no cycle", "null", () -> describeCycle(hexagonal(), findCycle(hexagonal())));
        check("two contexts that use each other form a cycle", "cycle through [catalogue, lending]", () -> {
            Map<String, Set<String>> deps = Map.of("app", Set.of("lending"), "lending", Set.of("catalogue"), "catalogue", Set.of("lending"));
            return describeCycle(deps, findCycle(deps));
        });
        check("a longer loop is found too", "cycle through [a, b, c]", () -> {
            Map<String, Set<String>> deps = Map.of("a", Set.of("b"), "b", Set.of("c"), "c", Set.of("a"), "d", Set.of("a"));
            return describeCycle(deps, findCycle(deps));
        });
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
