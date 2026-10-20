// Fix 2: never wait forever. Try both locks with a timeout; on failure, let go and retry later.
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class TryLock {
    static class Account {
        final ReentrantLock lock = new ReentrantLock();
        long balance = 1_000;
    }

    static final AtomicInteger retries = new AtomicInteger();

    static void transfer(Account from, Account to, long amount) throws InterruptedException {
        while (true) {
            if (from.lock.tryLock(1, TimeUnit.MILLISECONDS)) {
                try {
                    if (to.lock.tryLock(1, TimeUnit.MILLISECONDS)) {
                        try {
                            from.balance -= amount;
                            to.balance += amount;
                            return;                              // success
                        } finally {
                            to.lock.unlock();
                        }
                    }
                } finally {
                    from.lock.unlock();                          // give up the first lock too
                }
            }
            retries.incrementAndGet();
            Thread.sleep(0, ThreadLocalRandom.current().nextInt(1_000, 100_000)); // random back-off
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Account alice = new Account(), bob = new Account();
        Thread t1 = new Thread(() -> run(() -> transfer(alice, bob, 1)));
        Thread t2 = new Thread(() -> run(() -> transfer(bob, alice, 1)));
        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.println("total=" + (alice.balance + bob.balance) + " after 20,000 transfers, "
                + retries.get() + " retries");
    }

    interface Step { void go() throws InterruptedException; }
    static void run(Step step) {
        try { for (int i = 0; i < 10_000; i++) step.go(); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
