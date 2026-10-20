// 4 threads count words from L3's text into one shared map. Three versions of the map.
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class WordIndex {
    static final String TEXT = "The cat sat on the mat. The mat was flat,\nand the cat was fat! A cat, a mat, a hat.";
    static final String[] WORDS = TEXT.toLowerCase().split("[^a-z]+");
    static final int THREADS = 4, COPIES = 25_000;         // 100,000 copies of the text in total

    interface Counter { void add(String w); Map<String, Long> snapshot(); }

    static Map<String, Long> run(Counter counter) throws InterruptedException {
        Thread[] ts = new Thread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            ts[t] = new Thread(() -> {
                for (int c = 0; c < COPIES; c++) for (String w : WORDS) counter.add(w);
            });
            ts[t].start();
        }
        for (Thread t : ts) t.join();
        return new TreeMap<>(counter.snapshot());
    }

    static void report(String name, Map<String, Long> counts) {
        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        System.out.println(name + " total=" + total + " the=" + counts.get("the") + " cat=" + counts.get("cat"));
    }

    public static void main(String[] args) throws InterruptedException {
        Map<String, Long> plain = new HashMap<>();
        Map<String, Long> broken = run(new Counter() {
            public void add(String w) { plain.merge(w, 1L, Long::sum); }   // HashMap: not thread-safe
            public Map<String, Long> snapshot() { return plain; }
        });

        ConcurrentHashMap<String, Long> chm = new ConcurrentHashMap<>();
        Map<String, Long> merged = run(new Counter() {
            public void add(String w) { chm.merge(w, 1L, Long::sum); }
            public Map<String, Long> snapshot() { return chm; }
        });

        ConcurrentHashMap<String, LongAdder> adders = new ConcurrentHashMap<>();
        Map<String, Long> added = run(new Counter() {
            public void add(String w) { adders.computeIfAbsent(w, k -> new LongAdder()).increment(); }
            public Map<String, Long> snapshot() {
                Map<String, Long> m = new HashMap<>();
                adders.forEach((k, v) -> m.put(k, v.sum()));
                return m;
            }
        });

        report("HashMap      ", broken);
        report("CHM merge    ", merged);
        report("CHM LongAdder", added);
    }
}
