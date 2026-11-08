import java.util.*;

// Emits the pathviz.js spec used in L49, from real runs of the same algorithms as
// Dijkstra.java (lazy, ties broken by name) and Kruskal.java (weighted quick-union + path compression).
// Usage (from .build/): java l49/viz/PathTrace.java
public class PathTrace {
    record Road(String a, String b, int m) {}
    record Entry(int dist, String town) {}

    static final List<String> TOWNS = List.of("A", "B", "C", "D", "E", "F", "G");
    static final List<Road> ROADS = List.of(
        new Road("A", "B", 4), new Road("A", "C", 2), new Road("B", "C", 1), new Road("B", "D", 5),
        new Road("C", "D", 8), new Road("C", "E", 10), new Road("D", "E", 2), new Road("D", "F", 6),
        new Road("E", "F", 2), new Road("F", "G", 3), new Road("E", "G", 4));
    static final Map<String, int[]> POS = new LinkedHashMap<>();
    static {
        POS.put("A", new int[] {40, 120}); POS.put("B", new int[] {150, 40}); POS.put("C", new int[] {150, 200});
        POS.put("D", new int[] {280, 40}); POS.put("E", new int[] {280, 200}); POS.put("F", new int[] {380, 120});
        POS.put("G", new int[] {450, 200});
    }

    static String q(String s) { return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""; }
    static String strs(Collection<String> xs) { StringJoiner j = new StringJoiner(",", "[", "]"); xs.forEach(x -> j.add(q(x))); return j.toString(); }
    static String pairs(List<String[]> es) { StringJoiner j = new StringJoiner(",", "[", "]"); es.forEach(e -> j.add("[" + q(e[0]) + "," + q(e[1]) + "]")); return j.toString(); }
    static String c(String s) { return "<code>" + s + "</code>"; }

    static String dijkstra() {
        Map<String, List<Road>> g = new LinkedHashMap<>();
        for (Road r : ROADS) {
            g.computeIfAbsent(r.a(), k -> new ArrayList<>()).add(new Road(r.a(), r.b(), r.m()));
            g.computeIfAbsent(r.b(), k -> new ArrayList<>()).add(new Road(r.b(), r.a(), r.m()));
        }
        Map<String, Integer> dist = new LinkedHashMap<>();
        Map<String, String> parent = new LinkedHashMap<>();
        List<String> settled = new ArrayList<>();
        PriorityQueue<Entry> pq = new PriorityQueue<>(Comparator.comparingInt(Entry::dist).thenComparing(Entry::town));
        List<String> steps = new ArrayList<>();
        dist.put("A", 0);
        pq.add(new Entry(0, "A"));
        steps.add(dstep(null, false, settled, dist, pq, parent, List.of(),
            "Start: " + c("dist[A] = 0") + ", every other town is ∞. The queue holds one entry, " + c("(0, A)") + "."));
        while (!pq.isEmpty()) {
            Entry e = pq.poll();
            String v = e.town();
            if (settled.contains(v)) {
                steps.add(dstep(v, true, settled, dist, pq, parent, List.of(),
                    "Poll " + c("(" + e.dist() + ", " + v + ")") + ": <b>stale</b>. " + v + " was settled earlier at " + dist.get(v)
                        + ", so this older, longer entry is skipped. That's lazy deletion."));
                continue;
            }
            settled.add(v);
            List<String[]> relaxed = new ArrayList<>();
            List<String> notes = new ArrayList<>();
            for (Road r : g.get(v)) {
                String w = r.b();
                if (settled.contains(w)) continue;
                int nd = e.dist() + r.m();
                Integer old = dist.get(w);
                if (old == null || nd < old) {
                    dist.put(w, nd);
                    parent.put(w, v);
                    pq.add(new Entry(nd, w));
                    relaxed.add(new String[] {v, w});
                    notes.add(c(w) + " " + (old == null ? "∞" : String.valueOf(old)) + " → " + nd);
                } else {
                    notes.add(c(w) + " stays " + old + " (" + e.dist() + " + " + r.m() + " = " + nd + " is no better)");
                }
            }
            String what = notes.isEmpty() ? "No unsettled neighbours." : "Relax its roads: " + String.join("; ", notes) + ".";
            steps.add(dstep(v, false, settled, dist, pq, parent, relaxed,
                "Poll " + c("(" + e.dist() + ", " + v + ")") + ": the smallest distance in the queue, so " + v
                    + "'s distance is <b>final</b>. " + what));
        }
        return "{\"name\":\"Dijkstra from A\",\"kind\":\"dijkstra\",\"steps\":[" + String.join(",", steps) + "]}";
    }

    static String dstep(String cur, boolean stale, List<String> settled, Map<String, Integer> dist, PriorityQueue<Entry> pq,
                        Map<String, String> parent, List<String[]> relaxed, String note) {
        List<Entry> queue = new ArrayList<>(pq);
        queue.sort(Comparator.comparingInt(Entry::dist).thenComparing(Entry::town));
        StringJoiner d = new StringJoiner(",", "{", "}");
        dist.forEach((k, v) -> d.add(q(k) + ":" + v));
        StringJoiner pqj = new StringJoiner(",", "[", "]");
        for (Entry x : queue) pqj.add("[" + x.dist() + "," + q(x.town()) + "]");
        List<String[]> tree = new ArrayList<>();
        parent.forEach((k, v) -> tree.add(new String[] {v, k}));
        return "{\"cur\":" + (cur == null ? "null" : q(cur)) + ",\"stale\":" + stale + ",\"settled\":" + strs(settled) + ",\"dist\":" + d
            + ",\"pq\":" + pqj + ",\"tree\":" + pairs(tree) + ",\"hl\":" + pairs(relaxed) + ",\"note\":" + q(note) + "}";
    }

    static int[] parent, size;
    static int find(int p) {
        int root = p;
        while (root != parent[root]) root = parent[root];
        for (int x = p; x != root; ) { int next = parent[x]; parent[x] = root; x = next; }
        return root;
    }

    static String kruskal() {
        int n = TOWNS.size();
        parent = new int[n]; size = new int[n];
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
        List<Road> sorted = new ArrayList<>(ROADS);
        sorted.sort(Comparator.comparingInt(Road::m));
        List<String[]> tree = new ArrayList<>();
        List<String> steps = new ArrayList<>();
        int total = 0;
        steps.add(kstep(null, false, tree, "Every town is its own set: " + c("parent[x] = x") + ". The roads are sorted by minutes, lightest first."));
        for (Road r : sorted) {
            if (tree.size() == n - 1) {
                steps.add(kstep(null, false, tree, "That's " + (n - 1) + " roads for " + n + " towns: a spanning tree. Kruskal stops without looking at the rest. Total: <b>" + total + " minutes</b>."));
                break;
            }
            int p = TOWNS.indexOf(r.a()), qq = TOWNS.indexOf(r.b());
            int rp = find(p), rq = find(qq);
            String road = c(r.a() + "-" + r.b() + " " + r.m());
            if (rp == rq) {
                steps.add(kstep(r, false, tree, road + ": " + c("find(" + r.a() + ")") + " and " + c("find(" + r.b() + ")")
                    + " are both " + c(TOWNS.get(rp)) + ". Already connected, so this road would close a cycle: <b>skip</b>."));
                continue;
            }
            String how;
            if (size[rp] < size[rq]) { parent[rp] = rq; size[rq] += size[rp]; how = TOWNS.get(rp) + "'s set (size " + size[rp] + ") hangs under " + TOWNS.get(rq); }
            else { parent[rq] = rp; size[rp] += size[rq]; how = TOWNS.get(rq) + "'s set (size " + size[rq] + ") hangs under " + TOWNS.get(rp); }
            tree.add(new String[] {r.a(), r.b()});
            total += r.m();
            steps.add(kstep(r, true, tree, road + ": roots " + c(TOWNS.get(rp)) + " and " + c(TOWNS.get(rq)) + " differ, so <b>take</b> it. Union: " + how + "."));
        }
        return "{\"name\":\"Kruskal · union-find\",\"kind\":\"kruskal\",\"steps\":[" + String.join(",", steps) + "]}";
    }

    static String kstep(Road r, boolean take, List<String[]> tree, String note) {
        StringJoiner pj = new StringJoiner(",", "{", "}");
        for (int i = 0; i < TOWNS.size(); i++) pj.add(q(TOWNS.get(i)) + ":" + q(TOWNS.get(parent[i])));
        String edge = r == null ? "null" : "[" + q(r.a()) + "," + q(r.b()) + "]";
        return "{\"edge\":" + edge + ",\"take\":" + take + ",\"tree\":" + pairs(tree) + ",\"parent\":" + pj + ",\"note\":" + q(note) + "}";
    }

    public static void main(String[] args) {
        StringJoiner nodes = new StringJoiner(",", "{", "}");
        POS.forEach((k, v) -> nodes.add(q(k) + ":[" + v[0] + "," + v[1] + "]"));
        StringJoiner edges = new StringJoiner(",", "[", "]");
        for (Road r : ROADS) edges.add("[" + q(r.a()) + "," + q(r.b()) + "," + r.m() + "]");
        System.out.println("{\"nodes\":" + nodes + ",\"edges\":" + edges + ",\"modes\":[" + dijkstra() + "," + kruskal() + "]}");
    }
}
