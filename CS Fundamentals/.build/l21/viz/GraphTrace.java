import java.util.*;

// Emits the graphviz.js specs used in L21, from real runs of the same algorithms
// as Bfs.java, Dfs.java (stack, reversed push) and GridPaths.java.
// Usage: java GraphTrace.java bfsdfs | java GraphTrace.java dp
public class GraphTrace {
    static final Map<String, List<String>> G = new LinkedHashMap<>();
    static {
        G.put("A", List.of("B", "C")); G.put("B", List.of("A", "D")); G.put("C", List.of("A", "D", "E"));
        G.put("D", List.of("B", "C", "F")); G.put("E", List.of("C", "F", "H")); G.put("F", List.of("D", "E", "G"));
        G.put("G", List.of("F")); G.put("H", List.of("E"));
    }

    static String q(String s) { return "\"" + s.replace("\"", "\\\"") + "\""; }
    static String arr(Collection<String> xs) { StringJoiner j = new StringJoiner(",", "[", "]"); xs.forEach(x -> j.add(q(x))); return j.toString(); }
    static String edges(List<String[]> es) { StringJoiner j = new StringJoiner(",", "[", "]"); es.forEach(e -> j.add("[" + q(e[0]) + "," + q(e[1]) + "]")); return j.toString(); }
    static String code(String s) { return "<code>" + s + "</code>"; }
    static String list(List<String> xs) { return xs.isEmpty() ? "none" : String.join(", ", xs.stream().map(GraphTrace::code).toList()); }

    static String step(String cur, Collection<String> seen, List<String> done, Collection<String> ds, List<String[]> tree, String note) {
        return "{\"cur\":" + (cur == null ? "null" : q(cur)) + ",\"seen\":" + arr(seen) + ",\"done\":" + arr(done)
            + ",\"ds\":" + arr(ds) + ",\"tree\":" + edges(tree) + ",\"note\":" + q(note) + "}";
    }

    static String bfs() {
        List<String> steps = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>(List.of("A"));
        List<String> done = new ArrayList<>();
        List<String[]> tree = new ArrayList<>();
        Deque<String> queue = new ArrayDeque<>(List.of("A"));
        steps.add(step(null, seen, done, queue, tree, "Start: mark " + code("A") + " visited and put it in the queue."));
        while (!queue.isEmpty()) {
            String v = queue.removeFirst();
            done.add(v);
            List<String> added = new ArrayList<>(), skipped = new ArrayList<>();
            for (String n : G.get(v)) {
                if (seen.add(n)) { queue.addLast(n); tree.add(new String[] {v, n}); added.add(n); }
                else skipped.add(n);
            }
            String note = "Dequeue " + code(v) + " from the <b>front</b>. "
                + (added.isEmpty() ? "No new neighbours" : "New: " + list(added) + " (mark visited, parent " + code(v) + ", enqueue at the back)")
                + (skipped.isEmpty() ? "." : "; already visited: " + list(skipped) + ".");
            steps.add(step(v, seen, done, queue, tree, note));
        }
        return "{\"name\":\"BFS · queue\",\"ds\":\"queue\",\"steps\":[" + String.join(",", steps) + "]}";
    }

    static Set<String> seenNow(Set<String> visited, Deque<String> stack) {
        Set<String> s = new LinkedHashSet<>(visited);
        s.addAll(stack);
        return s;
    }

    static String dfs() {
        List<String> steps = new ArrayList<>();
        Set<String> visited = new LinkedHashSet<>();
        List<String> done = new ArrayList<>();
        List<String[]> tree = new ArrayList<>();
        Map<String, String> pushedBy = new HashMap<>();
        Deque<String> stack = new ArrayDeque<>(List.of("A"));
        steps.add(step(null, seenNow(visited, stack), done, stack, tree, "Start: push " + code("A") + " onto the stack."));
        while (!stack.isEmpty()) {
            String v = stack.removeLast();
            if (!visited.add(v)) {
                steps.add(step(v, seenNow(visited, stack), done, stack, tree, "Pop " + code(v) + ": already visited (it was pushed twice), so skip it."));
                continue;
            }
            done.add(v);
            if (pushedBy.containsKey(v)) tree.add(new String[] {pushedBy.get(v), v});
            List<String> pushed = new ArrayList<>();
            for (String n : G.get(v).reversed()) {
                if (!visited.contains(n)) { stack.addLast(n); pushedBy.put(n, v); pushed.add(n); }
            }
            String note = "Pop " + code(v) + " from the <b>top</b> and visit it. "
                + (pushed.isEmpty() ? "Nothing new to push, so the search backtracks." : "Push unvisited neighbours (reversed): " + list(pushed) + ".");
            steps.add(step(v, seenNow(visited, stack), done, stack, tree, note));
        }
        return "{\"name\":\"DFS · stack\",\"ds\":\"stack\",\"steps\":[" + String.join(",", steps) + "]}";
    }

    static String dp() {
        String[] grid = {"....", ".#..", "...#", "#..."};
        int R = grid.length, C = grid[0].length();
        long[][] dp = new long[R][C];
        List<String> steps = new ArrayList<>();
        StringJoiner blocked = new StringJoiner(",", "[", "]");
        for (int r = 0; r < R; r++) for (int c = 0; c < C; c++) if (grid[r].charAt(c) == '#') blocked.add("[" + r + "," + c + "]");
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                String note; String from = "[]";
                if (grid[r].charAt(c) == '#') { dp[r][c] = 0; note = "Wall: 0 paths can pass through here."; }
                else if (r == 0 && c == 0) { dp[r][c] = 1; note = "Base case: one way to be at the start (stand still)."; }
                else {
                    long up = r > 0 ? dp[r - 1][c] : 0, left = c > 0 ? dp[r][c - 1] : 0;
                    dp[r][c] = up + left;
                    StringJoiner f = new StringJoiner(",", "[", "]");
                    if (r > 0) f.add("[" + (r - 1) + "," + c + "]");
                    if (c > 0) f.add("[" + r + "," + (c - 1) + "]");
                    from = f.toString();
                    note = "above " + (r > 0 ? up : "(edge) 0") + " + left " + (c > 0 ? left : "(edge) 0") + " = <b>" + dp[r][c] + "</b>";
                }
                steps.add("{\"r\":" + r + ",\"c\":" + c + ",\"v\":\"" + dp[r][c] + "\",\"from\":" + from + ",\"note\":" + q("(" + r + "," + c + ") " + note) + "}");
            }
        }
        return "{\"kind\":\"table\",\"rows\":" + R + ",\"cols\":" + C + ",\"blocked\":" + blocked + ",\"steps\":[" + String.join(",", steps) + "]}";
    }

    public static void main(String[] args) {
        if (args[0].equals("dp")) { System.out.println(dp()); return; }
        String nodes = "{\"A\":[40,115],\"B\":[140,45],\"C\":[140,185],\"D\":[250,45],\"E\":[250,185],\"F\":[350,115],\"G\":[440,115],\"H\":[350,215]}";
        List<String[]> es = new ArrayList<>();
        for (var e : G.entrySet()) for (String n : e.getValue()) if (e.getKey().compareTo(n) < 0) es.add(new String[] {e.getKey(), n});
        System.out.println("{\"kind\":\"graph\",\"nodes\":" + nodes + ",\"edges\":" + edges(es) + ",\"modes\":[" + bfs() + "," + dfs() + "]}");
    }
}
