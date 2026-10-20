import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

public class Atomics {
    static final LongAdder retries = new LongAdder();     // counts failed compare-and-set attempts

    static void casIncrement(AtomicInteger a) {
        while (true) {
            int old = a.get();                              // 1. read
            if (a.compareAndSet(old, old + 1)) return;      // 2. write only if nobody changed it
            retries.increment();                            // 3. somebody did: loop and try again
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger atomic = new AtomicInteger();
        AtomicInteger manual = new AtomicInteger();
        LongAdder adder = new LongAdder();
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 1_000_000; j++) {
                    atomic.incrementAndGet();
                    casIncrement(manual);
                    adder.increment();
                }
            });
            t.start();
            workers.add(t);
        }
        for (Thread t : workers) t.join();
        System.out.println("incrementAndGet: " + atomic.get());
        System.out.println("my CAS loop:     " + manual.get() + " after " + retries.sum() + " retries");
        System.out.println("LongAdder:       " + adder.sum());
    }
}
