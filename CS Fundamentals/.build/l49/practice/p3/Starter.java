import java.util.*;

public class Practice {
    static class DisjointSets {
        final int[] parent;                       // package-visible so the checks can inspect the trees
        private final int[] size;                 // size[r] is only meaningful when r is a root

        DisjointSets(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
        }

        int find(int p) {
            throw new UnsupportedOperationException("TODO: walk to the root, then point every node on the path straight at it");
        }

        // Returns false if p and q were already connected.
        boolean union(int p, int q) {
            throw new UnsupportedOperationException("TODO: link the smaller tree's root under the larger one's");
        }

        int sizeOf(int p) {
            throw new UnsupportedOperationException("TODO: the number of sites in p's component");
        }
    }

    // CHECKS
    static class NaiveSets {                       // quick-find: slow but obviously right
        final int[] id;
        NaiveSets(int n) { id = new int[n]; for (int i = 0; i < n; i++) id[i] = i; }
        boolean connected(int p, int q) { return id[p] == id[q]; }
        boolean union(int p, int q) {
            int a = id[p], b = id[q];
            if (a == b) return false;
            for (int i = 0; i < id.length; i++) if (id[i] == a) id[i] = b;
            return true;
        }
        int sizeOf(int p) { int c = 0; for (int x : id) if (x == id[p]) c++; return c; }
    }

    static List<Integer> pathOf(DisjointSets s, int p) {
        List<Integer> path = new ArrayList<>(List.of(p));
        while (s.parent[p] != p) { p = s.parent[p]; path.add(p); }
        return path;
    }

    public static void main(String[] args) {
        check("every site starts alone", true, () -> {
            DisjointSets s = new DisjointSets(5);
            for (int p = 0; p < 5; p++) if (s.find(p) != p || s.sizeOf(p) != 1) return false;
            return true;
        });
        check("union reports whether it merged", List.of(true, false, 2, 1), () -> {
            DisjointSets s = new DisjointSets(3);
            return List.of(s.union(0, 1), s.union(1, 0), s.sizeOf(1), s.sizeOf(2));
        });
        check("the smaller tree goes under the larger, in either argument order", List.of(true, true), () -> {
            List<Boolean> ok = new ArrayList<>();
            for (boolean smallFirst : new boolean[] {true, false}) {
                DisjointSets s = new DisjointSets(3);
                s.union(0, 1);
                int root = s.find(0);
                if (smallFirst) s.union(2, 0); else s.union(0, 2);
                ok.add(s.find(2) == root && s.sizeOf(2) == 3);
            }
            return ok;
        });
        check("find points every node on its path at the root", "path of 4, all at the root", () -> {
            DisjointSets s = new DisjointSets(8);
            int[][] pairs = {{0, 1}, {2, 3}, {4, 5}, {6, 7}, {0, 2}, {4, 6}, {0, 4}};   // equal merges: height 3
            for (int[] pq : pairs) s.union(pq[0], pq[1]);
            int deepest = 0;
            for (int p = 0; p < 8; p++) if (pathOf(s, p).size() > pathOf(s, deepest).size()) deepest = p;
            List<Integer> path = pathOf(s, deepest);
            int root = s.find(deepest);
            for (int x : path) if (s.parent[x] != root) return "parent of " + x + " is " + s.parent[x];
            return "path of " + path.size() + ", all at the root";
        });
        check("agrees with a naive union-find on 500 random operations", "all 500 agree", () -> {
            Random rnd = new Random(49);
            int n = 12;
            DisjointSets fast = new DisjointSets(n);
            NaiveSets slow = new NaiveSets(n);
            for (int t = 0; t < 500; t++) {
                int p = rnd.nextInt(n), q = rnd.nextInt(n);
                if (slow.union(p, q) != fast.union(p, q)) return "step " + t + ": union(" + p + ", " + q + ")";
                int a = rnd.nextInt(n), b = rnd.nextInt(n);
                if (slow.connected(a, b) != (fast.find(a) == fast.find(b))) return "step " + t + ": connected(" + a + ", " + b + ")";
                if (slow.sizeOf(a) != fast.sizeOf(a)) return "step " + t + ": sizeOf(" + a + ")";
            }
            return "all 500 agree";
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
