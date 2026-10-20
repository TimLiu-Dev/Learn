package demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;
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

    // One green run proves little, so each fix is hammered five times.
    private static void check(Supplier<Counter> make) throws InterruptedException {
        for (int i = 0; i < 5; i++) assertEquals(800_000, hammer(make.get(), 8, 100_000));
    }

    @Test void synchronizedCounterNeverLosesAnUpdate() throws InterruptedException { check(SyncCounter::new); }
    @Test void reentrantLockCounterNeverLosesAnUpdate() throws InterruptedException { check(LockCounter::new); }
    @Test void atomicIntegerCounterNeverLosesAnUpdate() throws InterruptedException { check(AtomicCounter::new); }
}
