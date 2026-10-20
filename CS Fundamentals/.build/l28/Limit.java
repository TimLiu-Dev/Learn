import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Limit {
    static final Semaphore DB_PERMITS = new Semaphore(10);   // the "database" accepts 10 connections
    static final AtomicInteger IN_FLIGHT = new AtomicInteger();
    static final AtomicInteger MAX_SEEN = new AtomicInteger();

    static int query(int id) throws InterruptedException {
        DB_PERMITS.acquire();                     // virtual thread parks here if all 10 are taken
        try {
            int now = IN_FLIGHT.incrementAndGet();
            MAX_SEEN.accumulateAndGet(now, Math::max);
            Thread.sleep(20);                     // the query itself
            return id;
        } finally {
            IN_FLIGHT.decrementAndGet();
            DB_PERMITS.release();
        }
    }

    public static void main(String[] args) {
        long start = System.nanoTime();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {     // 1,000 virtual threads...
                int id = i;
                ex.submit(() -> query(id));
            }
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println("1000 queries, at most " + MAX_SEEN.get() + " at once, " + ms + " ms");  // ...but only 10 in the DB
    }
}
