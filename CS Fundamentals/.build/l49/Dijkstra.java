import java.util.*;

public class Dijkstra {
    record Road(String to, int minutes) {}
    record Entry(int dist, String town) {}

    static final Map<String, List<Road>> GRAPH = new LinkedHashMap<>();
    static {
        Object[][] roads = {
            {"A", "B", 4}, {"A", "C", 2}, {"B", "C", 1}, {"B", "D", 5},
            {"C", "D", 8}, {"C", "E", 10}, {"D", "E", 2}, {"D", "F", 6},
            {"E", "F", 2}, {"F", "G", 3}, {"E", "G", 4},
        };
        // Two-way roads: each one goes into both towns' adjacency lists.
        for (Object[] r : roads) {
            String a = (String) r[0], b = (String) r[1];
            int m = (Integer) r[2];
            GRAPH.computeIfAbsent(a, k -> new ArrayList<>()).add(new Road(b, m));
            GRAPH.computeIfAbsent(b, k -> new ArrayList<>()).add(new Road(a, m));
        }
    }

    static Map<String, Integer> dist = new HashMap<>();
    static Map<String, String> parent = new HashMap<>();

    static void dijkstra(String start) {
        dist.put(start, 0);
        parent.put(start, null);
        Set<String> settled = new HashSet<>();
        // Smallest distance first; ties broken by name so every run is identical.
        PriorityQueue<Entry> pq = new PriorityQueue<>(
            Comparator.comparingInt(Entry::dist).thenComparing(Entry::town));
        pq.add(new Entry(0, start));
        while (!pq.isEmpty()) {
            Entry e = pq.poll();
            int d = e.dist();
            String v = e.town();
            if (!settled.add(v)) {                       // an old, longer entry: skip it
                System.out.println("poll (" + d + ", " + v + ")  stale, " + v + " is already settled");
                continue;
            }
            System.out.println("poll (" + d + ", " + v + ")  settle " + v);
            for (Road r : GRAPH.get(v)) {
                String w = r.to();
                if (settled.contains(w)) continue;
                int nd = d + r.minutes();
                if (nd < dist.getOrDefault(w, Integer.MAX_VALUE)) {   // relax v-w: a shorter way to w
                    dist.put(w, nd);
                    parent.put(w, v);
                    pq.add(new Entry(nd, w));
                    System.out.println("    relax " + v + "-" + w + "  dist[" + w + "] = " + nd);
                }
            }
        }
    }

    public static void main(String[] args) {
        dijkstra("A");
        System.out.println(new TreeMap<>(dist));
        List<String> path = new ArrayList<>();
        for (String v = "G"; v != null; v = parent.get(v)) path.add(v);
        Collections.reverse(path);
        System.out.println("A -> G: " + path + ", " + dist.get("G") + " minutes");
    }
}
