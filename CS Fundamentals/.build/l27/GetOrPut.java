// 8 threads ask for the same missing key at once. How many times does the value get built?
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class GetOrPut {
    static class Connection { }                      // pretend this is expensive to open

    static void race(String name,
                     BiFunction<ConcurrentHashMap<String, Connection>, Supplier<Connection>, Connection> lookup)
            throws InterruptedException {
        ConcurrentHashMap<String, Connection> map = new ConcurrentHashMap<>();
        AtomicInteger built = new AtomicInteger();
        CountDownLatch go = new CountDownLatch(1);
        Set<Connection> seen = ConcurrentHashMap.newKeySet();
        Supplier<Connection> make = () -> {
            built.incrementAndGet();
            try { Thread.sleep(20); } catch (InterruptedException e) { }
            return new Connection();
        };
        Thread[] threads = new Thread[8];
        for (int i = 0; i < 8; i++) {
            threads[i] = new Thread(() -> {
                try { go.await(); } catch (InterruptedException e) { }
                seen.add(lookup.apply(map, make));
            });
            threads[i].start();
        }
        go.countDown();
        for (Thread t : threads) t.join();
        System.out.println(name + " built " + built.get() + ", map holds " + map.size()
                + ", callers saw " + seen.size());
    }

    public static void main(String[] args) throws InterruptedException {
        // What Kotlin's ConcurrentMap.getOrPut does: build first, then putIfAbsent
        race("get + putIfAbsent", (m, make) -> {
            Connection c = m.get("db");
            if (c != null) return c;
            Connection fresh = make.get();
            Connection prev = m.putIfAbsent("db", fresh);
            return prev != null ? prev : fresh;
        });
        race("computeIfAbsent  ", (m, make) -> m.computeIfAbsent("db", k -> make.get()));
    }
}
