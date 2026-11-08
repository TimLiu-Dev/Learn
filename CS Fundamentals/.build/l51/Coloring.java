import java.util.ArrayList;
import java.util.List;

public class Coloring {
    record Edge(int u, int v) {}
    record Graph(String name, int vertices, List<Edge> edges) {}

    // The reduction: 3-COLORING -> SAT. Variable x(v, c) means "vertex v gets colour c".
    static int x(int v, int c) { return 3 * v + c + 1; }

    static List<int[]> toCnf(int vertices, List<Edge> edges) {
        List<int[]> f = new ArrayList<>();
        for (int v = 0; v < vertices; v++) {
            f.add(new int[] {x(v, 0), x(v, 1), x(v, 2)});                       // at least one colour
            for (int c = 0; c < 3; c++)
                for (int d = c + 1; d < 3; d++) f.add(new int[] {-x(v, c), -x(v, d)});  // at most one
        }
        for (Edge e : edges)
            for (int c = 0; c < 3; c++) f.add(new int[] {-x(e.u(), c), -x(e.v(), c)}); // neighbours differ
        return f;
    }

    // A backtracking SAT solver (L50): set x1, x2, ... in turn, and abandon a branch
    // as soon as some clause has all of its literals false.
    static int nodes;
    static int[] solve(int n, List<int[]> f) {
        int[] value = new int[n + 1];                     // 0 = unset, 1 = true, -1 = false
        nodes = 0;
        return go(1, n, f, value) ? value : null;
    }

    static boolean go(int v, int n, List<int[]> f, int[] value) {
        nodes++;
        for (int[] clause : f) if (falsified(clause, value)) return false;   // prune
        if (v > n) return true;
        for (int choice : new int[] {1, -1}) { value[v] = choice; if (go(v + 1, n, f, value)) return true; }
        value[v] = 0;
        return false;
    }

    static boolean falsified(int[] clause, int[] value) {
        for (int lit : clause) if (value[Math.abs(lit)] != (lit > 0 ? -1 : 1)) return false;
        return true;
    }

    static List<Edge> wheel(int rim) {                    // hub 0, rim 1..rim
        List<Edge> e = new ArrayList<>();
        for (int i = 1; i <= rim; i++) { e.add(new Edge(0, i)); e.add(new Edge(i, i % rim + 1)); }
        return e;
    }

    static List<Edge> petersen() {
        List<Edge> e = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            e.add(new Edge(i, (i + 1) % 5)); e.add(new Edge(i, i + 5)); e.add(new Edge(i + 5, (i + 2) % 5 + 5));
        }
        return e;
    }

    public static void main(String[] args) {
        List<Graph> graphs = List.of(new Graph("wheel, 5-cycle rim", 6, wheel(5)),
            new Graph("wheel, 6-cycle rim", 7, wheel(6)), new Graph("Petersen graph", 10, petersen()));
        for (Graph g : graphs) {
            List<int[]> f = toCnf(g.vertices(), g.edges());
            int[] value = solve(3 * g.vertices(), f);
            System.out.printf("%-20s V=%2d E=%2d -> %2d vars, %3d clauses: ",
                g.name(), g.vertices(), g.edges().size(), 3 * g.vertices(), f.size());
            if (value == null) { System.out.println("UNSAT, so no 3-colouring (" + nodes + " nodes)"); continue; }
            int[] colour = new int[g.vertices()];
            StringBuilder sb = new StringBuilder();
            for (int v = 0; v < g.vertices(); v++) {                          // decode
                for (int c = 0; c < 3; c++) if (value[x(v, c)] == 1) colour[v] = c;
                sb.append(colour[v]);
            }
            boolean proper = g.edges().stream().allMatch(e -> colour[e.u()] != colour[e.v()]);
            System.out.println("SAT " + sb + " proper=" + proper + " (" + nodes + " nodes)");
        }
    }
}
