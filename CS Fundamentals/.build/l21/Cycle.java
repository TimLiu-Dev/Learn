import java.util.*;

public class Cycle {
    enum Colour { WHITE, GREY, BLACK }   // not started, on the current path, finished

    static Map<String, List<String>> needs;
    static Map<String, Colour> colour;
    static List<String> path;            // the GREY vertices, in order
    static List<String> order;           // postorder: prerequisites come first

    // DFS over "needs" edges (task -> its prerequisites).
    // Returns a topological order, or the first cycle it finds.
    static String check(Map<String, List<String>> g) {
        needs = g;
        colour = new HashMap<>();
        for (String v : g.keySet()) colour.put(v, Colour.WHITE);
        path = new ArrayList<>();
        order = new ArrayList<>();
        for (String v : g.keySet()) {
            if (colour.get(v) == Colour.WHITE) {
                List<String> cycle = visit(v);
                if (cycle != null) return "cycle: " + String.join(" -> ", cycle);
            }
        }
        return "order: " + order;
    }

    static List<String> visit(String v) {
        colour.put(v, Colour.GREY);
        path.add(v);
        for (String n : needs.get(v)) {
            switch (colour.get(n)) {
                case GREY -> {                                 // back edge: a cycle
                    List<String> cycle = new ArrayList<>(path.subList(path.indexOf(n), path.size()));
                    cycle.add(n);
                    return cycle;
                }
                case WHITE -> {
                    List<String> cycle = visit(n);
                    if (cycle != null) return cycle;
                }
                case BLACK -> { }                              // finished earlier, by another route: no cycle through it
            }
        }
        path.remove(path.size() - 1);
        colour.put(v, Colour.BLACK);
        order.add(v);
        return null;
    }

    public static void main(String[] args) {
        Map<String, List<String>> build = new LinkedHashMap<>();
        build.put("compile", List.of());
        build.put("resources", List.of());
        build.put("classes", List.of("compile", "resources"));
        build.put("compileTest", List.of("classes"));
        build.put("test", List.of("compileTest"));
        build.put("jar", List.of("classes"));
        build.put("build", List.of("jar", "test"));
        System.out.println(check(build));

        Map<String, List<String>> circular = new LinkedHashMap<>();
        circular.put("a", List.of("c"));
        circular.put("b", List.of("a"));
        circular.put("c", List.of("b"));
        circular.put("d", List.of());
        System.out.println(check(circular));
    }
}
