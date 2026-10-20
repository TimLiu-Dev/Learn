// Two transfers lock the same two accounts in opposite order. A watchdog reports the deadlock.
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class Deadlock {
    static class Account {
        final String name;
        long balance = 100;
        Account(String name) { this.name = name; }
    }

    static void transfer(Account from, Account to, long amount) {
        synchronized (from) {
            pause(50);                                  // widen the window between the two locks
            synchronized (to) {
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Account alice = new Account("alice"), bob = new Account("bob");
        Thread t1 = new Thread(() -> transfer(alice, bob, 10), "t1 alice->bob");
        Thread t2 = new Thread(() -> transfer(bob, alice, 20), "t2 bob->alice");
        t1.setDaemon(true);                             // stuck daemons can't stop the JVM exiting
        t2.setDaemon(true);
        t1.start();
        t2.start();

        t1.join(2000);                                  // watchdog: give them 2 s
        t2.join(100);
        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        long[] ids = mx.findDeadlockedThreads();
        if (ids == null) {
            System.out.println("finished: alice=" + alice.balance + " bob=" + bob.balance);
            return;
        }
        System.out.println("DEADLOCK between " + ids.length + " threads:");
        for (ThreadInfo info : mx.getThreadInfo(ids)) {
            System.out.println("  " + info.getThreadName() + " is " + info.getThreadState()
                    + ", waiting for " + info.getLockName()
                    + " held by " + info.getLockOwnerName());
        }
    }

    static void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
