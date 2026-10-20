// Four ways to create an expensive object on first use. 8 threads ask at the same moment.
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class LazyInit {
    static final AtomicInteger created = new AtomicInteger();

    static class Expensive {
        Expensive() {
            created.incrementAndGet();
            try { Thread.sleep(20); } catch (InterruptedException e) { }  // widen the race window
        }
    }

    // 1. Racy: check-then-act with no lock
    static Expensive racy;
    static Expensive racyGet() {
        if (racy == null) racy = new Expensive();
        return racy;
    }

    // 2. Double-checked locking: correct ONLY because the field is volatile
    static volatile Expensive dcl;
    static Expensive dclGet() {
        Expensive result = dcl;                 // one volatile read on the fast path
        if (result == null) {
            synchronized (LazyInit.class) {
                result = dcl;
                if (result == null) dcl = result = new Expensive();
            }
        }
        return result;
    }

    // 3. Holder class: the JVM initialises Holder once, on first use, thread-safely
    static class Holder {
        static final Expensive INSTANCE = new Expensive();
    }
    static Expensive holderGet() { return Holder.INSTANCE; }

    static void race(String name, Supplier<Expensive> get) throws InterruptedException {
        created.set(0);
        CountDownLatch go = new CountDownLatch(1);
        Set<Expensive> seen = ConcurrentHashMap.newKeySet();
        Thread[] threads = new Thread[8];
        for (int i = 0; i < 8; i++) {
            threads[i] = new Thread(() -> {
                try { go.await(); } catch (InterruptedException e) { }
                seen.add(get.get());
            });
            threads[i].start();
        }
        go.countDown();                         // release all 8 at once
        for (Thread t : threads) t.join();
        System.out.println(name + ": created " + created.get() + " time(s), callers saw "
                + seen.size() + " instance(s)");
    }

    public static void main(String[] args) throws InterruptedException {
        race("racy        ", LazyInit::racyGet);
        race("double-check", LazyInit::dclGet);
        race("holder class", LazyInit::holderGet);
    }
}
