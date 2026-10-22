import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// The AI's cache pattern, isolated: 8 request threads filling one shared map.
public class CacheRace {
    static final AtomicInteger crashed = new AtomicInteger();

    /** The final size, or -1 if a worker is still running after 5 s. */
    static int fill(Map<String, Integer> cache) throws InterruptedException {
        crashed.set(0);
        List<Thread> workers = new ArrayList<>();
        for (int t = 1; t <= 8; t++) {
            int id = t;
            workers.add(Thread.ofPlatform().daemon().start(() -> {
                try {
                    for (int k = 0; k < 10_000; k++) {
                        int value = k;
                        cache.computeIfAbsent("t" + id + "-p" + k, key -> value);
                    }
                } catch (ConcurrentModificationException e) {
                    crashed.incrementAndGet();             // this worker dies; its remaining keys are never added
                }
            }));
        }
        long deadline = System.nanoTime() + 5_000_000_000L;
        for (Thread w : workers) w.join(Math.max(1, (deadline - System.nanoTime()) / 1_000_000));
        List<Thread> stuck = workers.stream().filter(Thread::isAlive).toList();
        if (stuck.isEmpty()) return cache.size();
        System.out.println("  STUCK: " + stuck.size() + " thread(s) still running after 5 s, e.g.");
        StackTraceElement[] frames = stuck.get(0).getStackTrace();
        for (int i = 0; i < Math.min(3, frames.length); i++) System.out.println("    at " + frames[i]);
        return -1;
    }

    public static void main(String[] args) throws InterruptedException {
        for (int run = 1; run <= 5; run++) {
            int size = fill(new HashMap<>());
            if (size < 0) { System.out.println("run " + run + ": HashMap never finished"); break; }
            int dead = crashed.get();
            System.out.println("run " + run + ": HashMap lost " + (80_000 - size) + " of 80000 entries ("
                    + dead + " of 8 threads died of ConcurrentModificationException), ConcurrentHashMap has "
                    + fill(new ConcurrentHashMap<>()));
        }
    }
}
