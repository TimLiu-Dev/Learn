import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Locks {
    static class LockCounter {
        private final ReentrantLock lock = new ReentrantLock();
        private int count;
        void increment() {
            lock.lock();
            try { count++; }
            finally { lock.unlock(); }       // always unlock, even if the body throws
        }
        int get() {
            lock.lock();
            try { return count; } finally { lock.unlock(); }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LockCounter c = new LockCounter();
        Runnable work = () -> { for (int i = 0; i < 1_000_000; i++) c.increment(); };
        Thread t1 = new Thread(work), t2 = new Thread(work);
        t1.start(); t2.start(); t1.join(); t2.join();
        System.out.println("ReentrantLock counter = " + c.get());

        ReentrantLock lock = new ReentrantLock();
        Thread holder = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("holder: got the lock, working for 300 ms");
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally { lock.unlock(); }
        });
        holder.start();
        Thread.sleep(50);
        if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
            try { System.out.println("main: got it"); } finally { lock.unlock(); }
        } else {
            System.out.println("main: gave up after 100 ms instead of waiting forever");
        }
        holder.join();
    }
}
