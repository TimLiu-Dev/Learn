// 4 threads, 10 accounts, 100,000 random transfers. Run with v1, v2 or v3.
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Random;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {
    static final int ACCOUNTS = 10, THREADS = 4, TRANSFERS = 100_000;

    static class Account {
        final int id;
        long balance = 1_000;
        Account(int id) { this.id = id; }
    }

    static final Account[] accounts = new Account[ACCOUNTS];
    static final LongAdder done = new LongAdder();
    static final ReentrantLock bankLock = new ReentrantLock();
    static volatile boolean running = true;                 // tells the monitor thread to stop

    static void move(Account from, Account to, long amount) {
        if (from.balance >= amount) {                       // no overdrafts
            from.balance -= amount;
            to.balance += amount;
        }
    }

    // v1: lock "from" then "to": two threads can take the same pair in opposite order
    static void transferV1(Account from, Account to, long amount) {
        synchronized (from) { synchronized (to) { move(from, to, amount); } }
    }

    // v2: always lock the lower id first
    static void transferV2(Account from, Account to, long amount) {
        Account first = from.id < to.id ? from : to, second = from.id < to.id ? to : from;
        synchronized (first) { synchronized (second) { move(from, to, amount); } }
    }

    // v3: one lock for the whole bank
    static void transferV3(Account from, Account to, long amount) {
        bankLock.lock();
        try { move(from, to, amount); } finally { bankLock.unlock(); }
    }

    public static void main(String[] args) throws InterruptedException {
        String version = args.length > 0 ? args[0] : "v1";
        for (int i = 0; i < ACCOUNTS; i++) accounts[i] = new Account(i);

        Thread monitor = new Thread(() -> {
            while (running) {
                try { Thread.sleep(500); } catch (InterruptedException e) { return; }
                if (running) System.out.println("  monitor: " + done.sum() + " transfers done");
            }
        });
        monitor.setDaemon(true);
        monitor.start();

        long start = System.nanoTime();
        Thread[] workers = new Thread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            Random rnd = new Random(42 + t);                // fixed seed per thread
            workers[t] = new Thread(() -> {
                for (int i = 0; i < TRANSFERS / THREADS; i++) {
                    Account from = accounts[rnd.nextInt(ACCOUNTS)];
                    Account to = accounts[rnd.nextInt(ACCOUNTS)];
                    if (from == to) { done.increment(); continue; }
                    long amount = 1 + rnd.nextInt(100);
                    switch (version) {
                        case "v1" -> transferV1(from, to, amount);
                        case "v2" -> transferV2(from, to, amount);
                        default -> transferV3(from, to, amount);
                    }
                    done.increment();
                }
            }, "worker-" + t);
            workers[t].setDaemon(true);
            workers[t].start();
        }

        long deadline = System.currentTimeMillis() + 2_000;   // watchdog: 2 s for everything
        for (Thread w : workers) w.join(Math.max(1, deadline - System.currentTimeMillis()));
        running = false;

        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        long[] stuck = mx.findDeadlockedThreads();
        if (stuck != null) {
            System.out.println(version + ": DEADLOCK after " + done.sum() + " transfers");
            for (ThreadInfo info : mx.getThreadInfo(stuck)) {
                System.out.println("  " + info.getThreadName() + " waits for " + info.getLockName()
                        + " held by " + info.getLockOwnerName());
            }
            return;
        }
        long total = 0;
        for (Account a : accounts) total += a.balance;
        System.out.printf("%s: %d transfers in %d ms, total %d (expected %d)%n", version,
                done.sum(), (System.nanoTime() - start) / 1_000_000, total, 1_000L * ACCOUNTS);
    }
}
