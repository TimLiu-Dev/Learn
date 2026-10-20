package demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CounterTest {
    private static int hammer(Counter counter, int threads, int perThread) throws InterruptedException {
        CountDownLatch ready = new CountDownLatch(threads);   // every worker has started...
        CountDownLatch go = new CountDownLatch(1);            // ...then one start signal for all
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> {
                ready.countDown();
                try { go.await(); } catch (InterruptedException e) { return; }
                for (int j = 0; j < perThread; j++) counter.increment();
            });
            t.start();
            workers.add(t);
        }
        ready.await();
        go.countDown();
        for (Thread t : workers) t.join();
        return counter.value();
    }

    @Test
    void eightThreadsTimes100kIncrementsGive800k() throws InterruptedException {
        assertEquals(800_000, hammer(new UnsafeCounter(), 8, 100_000));
    }
}
