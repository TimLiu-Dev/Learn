import java.util.*;

public class Milestone {
    static long sink = 0;   // results go here so the JIT can't delete the work as unused

    // Runs block 3 times and returns the fastest, in ms. The slower runs are mostly noise (GC, JIT).
    static double ms(Runnable block) {
        double best = Double.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            long t0 = System.nanoTime();
            block.run();
            best = Math.min(best, (System.nanoTime() - t0) / 1e6);
        }
        return best;
    }

    static Map<String, Double> measure(int n) {
        List<Integer> keys = new ArrayList<>();
        for (int i = 0; i < n; i++) keys.add(i);
        Collections.shuffle(keys, new Random(n));       // random order keeps the BST bushy
        Map<String, Double> r = new LinkedHashMap<>();
        r.put("MyArrayList", ms(() -> { var l = new MyArrayList<Integer>(); for (int k : keys) l.add(k); for (int i = 0; i < n; i++) sink += l.get(i); }));
        r.put("ArrayList", ms(() -> { var l = new ArrayList<Integer>(); for (int k : keys) l.add(k); for (int i = 0; i < n; i++) sink += l.get(i); }));
        r.put("MyHashMap", ms(() -> { var m = new MyHashMap<Integer, Integer>(); for (int k : keys) m.put(k, k); for (int k : keys) sink += m.get(k); }));
        r.put("HashMap", ms(() -> { var m = new HashMap<Integer, Integer>(); for (int k : keys) m.put(k, k); for (int k : keys) sink += m.get(k); }));
        r.put("MyBST", ms(() -> { var t = new MyBST<Integer, Integer>(); for (int k : keys) t.put(k, k); for (int k : keys) sink += t.get(k); }));
        r.put("TreeMap", ms(() -> { var t = new TreeMap<Integer, Integer>(); for (int k : keys) t.put(k, k); for (int k : keys) sink += t.get(k); }));
        return r;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) measure(200_000);   // warm-up: let the JIT compile everything
        int[] sizes = {250_000, 500_000, 1_000_000, 2_000_000};
        List<Map<String, Double>> results = new ArrayList<>();
        for (int n : sizes) results.add(measure(n));
        System.out.println("n added then read back; time in ms (ratio vs previous n)");
        StringBuilder header = new StringBuilder(String.format("%-12s", ""));
        for (int n : sizes) header.append(String.format("%16d", n));
        System.out.println(header);
        for (String name : results.get(0).keySet()) {
            StringBuilder row = new StringBuilder(String.format("%-12s", name));
            for (int i = 0; i < sizes.length; i++) {
                double t = results.get(i).get(name);
                row.append(i == 0 ? String.format("%16.1f", t)
                    : String.format("%9.1f (%.1f)", t, t / results.get(i - 1).get(name)));
            }
            System.out.println(row);
        }

        List<Integer> sorted = new ArrayList<>();          // the worst case for an unbalanced BST
        for (int i = 0; i < 20_000; i++) sorted.add(i);
        double bst = ms(() -> { var t = new MyBST<Integer, Integer>(); for (int k : sorted) t.put(k, k); for (int k : sorted) sink += t.get(k); });
        double tree = ms(() -> { var t = new TreeMap<Integer, Integer>(); for (int k : sorted) t.put(k, k); for (int k : sorted) sink += t.get(k); });
        System.out.printf("sorted keys, n=20000: MyBST %.1f ms, TreeMap %.1f ms%n", bst, tree);
    }
}
