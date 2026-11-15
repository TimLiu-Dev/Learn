import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class Practice {
    // Runs init at most once, however many threads call get() at the same moment.
    static final class Once<T> {
        final Object lock = new Object();
        private final Supplier<T> init;
        private volatile T value;                       // the fast path reads it without the lock

        Once(Supplier<T> init) { this.init = init; }

        T get() {
            T v = value;                                // 1st check, no lock: one volatile read
            if (v != null) return v;
            synchronized (lock) {
                if (value == null) value = init.get();  // 2nd check: another thread may have won
                return value;
            }
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("init runs once for one caller", 1, () -> {
            var calls = new AtomicInteger();
            var once = new Once<>(() -> { calls.incrementAndGet(); return "config"; });
            once.get(); once.get();
            return calls.get();
        });
        check("a second caller waits instead of running init again", "1 call, same object", Practice::race);
        check("once the value exists, get() doesn't touch the lock", "ready", Practice::readWhileLocked);
        check("the field is volatile", true, () -> Modifier.isVolatile(Once.class.getDeclaredField("value").getModifiers()));
    }

    static String race() throws Exception {
        var calls = new AtomicInteger();
        var insideInit = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var once = new Once<Object>(() -> {
            calls.incrementAndGet();
            insideInit.countDown();
            try { release.await(5, TimeUnit.SECONDS); } catch (InterruptedException e) { }
            return new Object();
        });
        Object[] got = new Object[2];
        Thread a = daemon(() -> got[0] = once.get());
        insideInit.await(5, TimeUnit.SECONDS);              // a is now inside init
        Thread b = daemon(() -> got[1] = once.get());
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (b.getState() != Thread.State.BLOCKED && b.getState() != Thread.State.WAITING
                && calls.get() < 2 && System.nanoTime() < deadline) Thread.onSpinWait();
        release.countDown();
        a.join(5000); b.join(5000);
        return calls.get() + (calls.get() == 1 ? " call" : " calls") + ", " + (got[0] == got[1] ? "same object" : "different objects");
    }

    static Object readWhileLocked() throws Exception {
        var once = new Once<>(() -> "ready");
        once.get();
        Object[] got = new Object[1];
        Thread reader;
        synchronized (once.lock) {                          // another thread is busy holding the lock
            reader = daemon(() -> got[0] = once.get());
            reader.join(2000);
        }
        return reader.isAlive() ? "still waiting for the lock" : got[0];
    }

    static Thread daemon(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
        return t;
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
