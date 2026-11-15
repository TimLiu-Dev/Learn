import java.util.*;

public class Practice {
    static List<List<Integer>> toList(int n, int[][] edges) {
        throw new UnsupportedOperationException("TODO: one list per vertex; each edge goes into both lists");
    }

    static boolean[][] toMatrix(int n, int[][] edges) {
        throw new UnsupportedOperationException("TODO: n × n booleans, true where there's an edge");
    }

    static List<Integer> isolated(List<List<Integer>> adj) {
        throw new UnsupportedOperationException("TODO: the vertices whose list is empty");
    }

    // CHECKS
    public static void main(String[] args) {
        check("every undirected edge appears in both lists", List.of(List.of(1), List.of(0, 2), List.of(1)), () -> toList(3, new int[][] {{0, 1}, {1, 2}}));
        check("a vertex with no edges still gets an empty list", "4 []", () -> { var adj = toList(4, new int[][] {{0, 1}}); return adj.size() + " " + adj.get(3); });
        check("an empty graph has no lists at all", List.of(), () -> toList(0, new int[0][]));
        check("the matrix is symmetric", "[[false, false, true], [false, false, false], [true, false, false]]", () -> Arrays.deepToString(toMatrix(3, new int[][] {{0, 2}})));
        check("isolated vertices are the ones with degree 0", List.of(3, 4), () -> isolated(toList(5, new int[][] {{0, 1}, {2, 1}})));
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
