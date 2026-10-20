import java.util.HashMap;
import java.util.Map;

public class MapRace {
    public static void main(String[] args) throws InterruptedException {
        for (int run = 1; run <= 5; run++) {
            Map<String, Integer> counts = new HashMap<>();
            Map<Integer, Integer> keys = new HashMap<>();
            Thread[] workers = new Thread[2];
            for (int w = 0; w < 2; w++) {
                int base = w * 100_000;
                workers[w] = new Thread(() -> {
                    for (int i = 0; i < 100_000; i++) {
                        String word = "w" + (i % 10);
                        counts.put(word, counts.getOrDefault(word, 0) + 1);   // read-modify-write
                        keys.put(base + i, i);                                // structural change: inserts, resizes
                    }
                });
                workers[w].start();
            }
            for (Thread t : workers) t.join();
            int total = counts.values().stream().mapToInt(Integer::intValue).sum();
            System.out.println("run " + run + ": total count = " + total + " (expected 200000), keys.size = " + keys.size() + " (expected 200000)");
        }
    }
}
