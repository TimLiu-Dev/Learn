import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class Bench {
    interface Counter { void increment(); long get(); }

    static class Unsafe implements Counter {
        private long n;
        public void increment() { n++; }
        public long get() { return n; }
    }
    static class Synced implements Counter {
        private long n;
        public synchronized void increment() { n++; }
        public synchronized long get() { return n; }
    }
    static class Locked implements Counter {
        private final ReentrantLock lock = new ReentrantLock(); private long n;
        public void increment() { lock.lock(); try { n++; } finally { lock.unlock(); } }
        public long get() { lock.lock(); try { return n; } finally { lock.unlock(); } }
    }
    static class Atomic implements Counter {
        private final AtomicInteger n = new AtomicInteger();
        public void increment() { n.incrementAndGet(); }
        public long get() { return n.get(); }
    }
    static class Adder implements Counter {
        private final LongAdder n = new LongAdder();
        public void increment() { n.increment(); }
        public long get() { return n.sum(); }
    }

    record Kind(String name, Supplier<Counter> make) {}

    static long[] measure(Supplier<Counter> make, int threads, int perThread) throws InterruptedException {
        Counter c = make.get();
        long start = System.nanoTime();
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> { for (int j = 0; j < perThread; j++) c.increment(); });
            t.start(); ts.add(t);
        }
        for (Thread t : ts) t.join();
        return new long[] { c.get(), (System.nanoTime() - start) / 1_000_000 };
    }

    public static void main(String[] args) throws InterruptedException {
        List<Kind> kinds = List.of(new Kind("unsafe", Unsafe::new), new Kind("synchronized", Synced::new),
            new Kind("ReentrantLock", Locked::new), new Kind("AtomicInteger", Atomic::new), new Kind("LongAdder", Adder::new));
        int perThread = 1_000_000;
        for (Kind k : kinds) measure(k.make(), 4, perThread);        // warm-up round, not printed
        System.out.println("cores: " + Runtime.getRuntime().availableProcessors() + ", " + perThread + " increments per thread");
        for (int threads : new int[] { 2, 4, 8 }) {
            System.out.println("--- " + threads + " threads, expected " + (long) threads * perThread);
            for (Kind k : kinds) {
                long[] r = measure(k.make(), threads, perThread);
                String ok = r[0] == (long) threads * perThread ? "correct" : "WRONG";
                System.out.println(String.format("%-14s %10d  %-7s %5d ms", k.name(), r[0], ok, r[1]));
            }
        }
    }
}
