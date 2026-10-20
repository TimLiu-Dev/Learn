// 4 threads count the same words into one ConcurrentHashMap: get-then-put vs merge.
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class ConcurrentCount {
    static final String[] WORDS = "the cat sat on the mat the end".split(" ");

    static Map<String, Integer> count(BiConsumer<Map<String, Integer>, String> add)
            throws InterruptedException {
        Map<String, Integer> counts = new ConcurrentHashMap<>();
        Thread[] threads = new Thread[4];
        for (int t = 0; t < 4; t++) {
            threads[t] = new Thread(() -> {
                for (int rep = 0; rep < 250_000; rep++)
                    for (String w : WORDS) add.accept(counts, w);
            });
            threads[t].start();
        }
        for (Thread t : threads) t.join();
        return counts;
    }

    public static void main(String[] args) throws InterruptedException {
        // Two separate atomic calls: another thread can update between them
        Map<String, Integer> broken = count((m, w) -> m.put(w, m.getOrDefault(w, 0) + 1));
        System.out.println("get + put: " + new java.util.TreeMap<>(broken));

        // One atomic read-modify-write
        Map<String, Integer> merged = count((m, w) -> m.merge(w, 1, Integer::sum));
        System.out.println("merge:     " + new java.util.TreeMap<>(merged));
    }
}
