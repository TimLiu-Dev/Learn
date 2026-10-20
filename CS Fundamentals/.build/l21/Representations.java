import java.util.*;

public class Representations {
    record Edge(String from, String to) {}

    // Undirected: every edge goes into BOTH vertices' lists.
    static Map<String, List<String>> undirected(List<Edge> edges) {
        Map<String, List<String>> adj = new LinkedHashMap<>();
        for (Edge e : edges) {
            adj.computeIfAbsent(e.from(), k -> new ArrayList<>()).add(e.to());
            adj.computeIfAbsent(e.to(), k -> new ArrayList<>()).add(e.from());
        }
        return adj;
    }

    public static void main(String[] args) {
        List<Edge> edges = List.of(
            new Edge("A", "B"), new Edge("A", "C"), new Edge("B", "D"), new Edge("C", "D"), new Edge("C", "E"),
            new Edge("D", "F"), new Edge("E", "F"), new Edge("F", "G"), new Edge("E", "H"));

        Map<String, List<String>> adj = undirected(edges);
        System.out.println("adjacency list:");
        for (var entry : adj.entrySet()) {
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
        }

        List<String> names = new ArrayList<>(adj.keySet());
        Collections.sort(names);
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < names.size(); i++) index.put(names.get(i), i);
        boolean[][] matrix = new boolean[names.size()][names.size()];
        for (Edge e : edges) {
            matrix[index.get(e.from())][index.get(e.to())] = true;
            matrix[index.get(e.to())][index.get(e.from())] = true;
        }
        System.out.println("adjacency matrix:");
        System.out.println("    " + String.join(" ", names));
        for (int i = 0; i < names.size(); i++) {
            StringBuilder row = new StringBuilder("  " + names.get(i));
            for (boolean b : matrix[i]) row.append(b ? " 1" : " .");
            System.out.println(row);
        }

        System.out.println("edge C-E?  list: " + adj.get("C").contains("E")
            + "  matrix: " + matrix[index.get("C")][index.get("E")]);
        System.out.println("neighbours of F: " + adj.get("F"));
        int slots = 0;
        for (List<String> ns : adj.values()) slots += ns.size();
        System.out.println("V=" + names.size() + " E=" + edges.size() + "  list slots=" + slots
            + "  matrix cells=" + names.size() * names.size());
    }
}
