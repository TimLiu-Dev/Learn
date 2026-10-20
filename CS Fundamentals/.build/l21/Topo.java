import java.util.*;

public class Topo {
    // Kahn's algorithm. needs: task -> the tasks that must run before it.
    static List<String> topoSort(Map<String, List<String>> needs) {
        Map<String, Integer> waitingOn = new LinkedHashMap<>();            // in-degree
        Map<String, List<String>> unlocks = new HashMap<>();               // reversed edges
        for (var e : needs.entrySet()) {
            waitingOn.put(e.getKey(), e.getValue().size());
            for (String b : e.getValue()) unlocks.computeIfAbsent(b, k -> new ArrayList<>()).add(e.getKey());
        }
        Deque<String> ready = new ArrayDeque<>();
        for (var e : waitingOn.entrySet()) if (e.getValue() == 0) ready.addLast(e.getKey());
        List<String> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            String task = ready.removeFirst();
            order.add(task);
            for (String next : unlocks.getOrDefault(task, List.of())) {
                waitingOn.merge(next, -1, Integer::sum);
                if (waitingOn.get(next) == 0) ready.addLast(next);
            }
        }
        return order.size() == needs.size() ? order : null;   // leftovers sit on a cycle
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
        System.out.println(topoSort(build));

        Map<String, List<String>> circular = new LinkedHashMap<>();
        circular.put("a", List.of("c"));
        circular.put("b", List.of("a"));
        circular.put("c", List.of("b"));
        circular.put("d", List.of());
        System.out.println(topoSort(circular));
    }
}
