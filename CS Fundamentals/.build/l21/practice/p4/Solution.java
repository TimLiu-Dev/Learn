import java.util.*;

public class Practice {
    static List<String> schedule(Map<String, List<String>> needs) {
        Map<String, Integer> waitingOn = new HashMap<>();
        Map<String, List<String>> unlocks = new HashMap<>();
        for (var e : needs.entrySet()) {
            waitingOn.put(e.getKey(), e.getValue().size());
            for (String b : e.getValue()) unlocks.computeIfAbsent(b, k -> new ArrayList<>()).add(e.getKey());
        }
        // "Repeatedly take the smallest" is the PriorityQueue row of the table.
        PriorityQueue<String> ready = new PriorityQueue<>();
        for (var e : waitingOn.entrySet()) if (e.getValue() == 0) ready.add(e.getKey());
        List<String> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            String task = ready.poll();
            order.add(task);
            for (String next : unlocks.getOrDefault(task, List.of())) {
                int left = waitingOn.merge(next, -1, Integer::sum);
                if (left == 0) ready.add(next);
            }
        }
        return order.size() == needs.size() ? order : null;
    }

    // CHECKS
    public static void main(String[] args) {
        check("ready tasks run in alphabetical order", List.of("a", "b", "c"), () -> schedule(Map.of("b", List.of(), "c", List.of(), "a", List.of())));
        check("a task unlocked later can jump the queue", List.of("b", "a", "z"), () -> schedule(Map.of("z", List.of(), "b", List.of(), "a", List.of("b"))));
        check("prerequisites always come first", List.of("compile", "resources", "classes", "compileTest", "jar", "test", "build"), () -> schedule(Map.of(
            "compile", List.of(), "resources", List.of(), "classes", List.of("compile", "resources"),
            "compileTest", List.of("classes"), "test", List.of("compileTest"),
            "jar", List.of("classes"), "build", List.of("jar", "test"))));
        check("a cycle gives null", null, () -> schedule(Map.of("a", List.of("c"), "b", List.of("a"), "c", List.of("b"), "d", List.of())));
        check("no tasks gives an empty schedule", List.of(), () -> schedule(Map.of()));
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
