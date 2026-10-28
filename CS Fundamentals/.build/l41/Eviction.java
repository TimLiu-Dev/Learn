import java.util.*;

// LRU vs LFU vs FIFO on a skewed (Zipf-like) workload: a few keys are very popular.
// Capacity 100, 1,000 keys, 200,000 requests, fixed seed.
public class Eviction {
    interface Policy { boolean access(int key); }        // true = hit

    static Policy lru(int cap) {                          // L19: access-order LinkedHashMap
        var m = new LinkedHashMap<Integer, Boolean>(16, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Integer, Boolean> e) { return size() > cap; }
        };
        return k -> { if (m.get(k) != null) return true; m.put(k, true); return false; };
    }
    static Policy fifo(int cap) {                         // insertion order: oldest goes first
        var m = new LinkedHashMap<Integer, Boolean>() {
            protected boolean removeEldestEntry(Map.Entry<Integer, Boolean> e) { return size() > cap; }
        };
        return k -> { if (m.containsKey(k)) return true; m.put(k, true); return false; };
    }
    static Policy lfu(int cap) {                          // evict the cached key used least often
        var count = new HashMap<Integer, Integer>();
        return k -> {
            if (count.containsKey(k)) { count.merge(k, 1, Integer::sum); return true; }
            if (count.size() == cap) {
                int victim = Collections.min(count.entrySet(), Map.Entry.comparingByValue()).getKey();
                count.remove(victim);
            }
            count.put(k, 1);
            return false;
        };
    }

    public static void main(String[] args) {
        int keys = 1_000, cap = 100, requests = 200_000;
        double[] cdf = new double[keys];                  // Zipf: P(rank r) is proportional to 1/r
        double total = 0;
        for (int r = 1; r <= keys; r++) { total += 1.0 / r; cdf[r - 1] = total; }

        for (boolean scans : new boolean[] {false, true}) {
            System.out.println(scans ? "Zipf + a one-off scan of 500 cold keys every 1,000 requests" : "Zipf only");
            for (String name : List.of("LRU", "LFU", "FIFO")) {
                Policy p = switch (name) { case "LRU" -> lru(cap); case "LFU" -> lfu(cap); default -> fifo(cap); };
                var rnd = new Random(41);
                int hits = 0, gets = 0;
                for (int i = 0; i < requests; i++) {
                    if (scans && i % 1_000 == 0)
                        for (int cold = 0; cold < 500; cold++) p.access(10_000 + i * 500 + cold);   // never seen again
                    int idx = Arrays.binarySearch(cdf, rnd.nextDouble() * total);
                    int key = idx >= 0 ? idx : -idx - 1;
                    gets++;
                    if (p.access(key)) hits++;
                }
                System.out.printf("  %-4s hit rate %.1f%%%n", name, 100.0 * hits / gets);
            }
        }
    }
}
