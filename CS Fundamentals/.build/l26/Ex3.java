import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Ex3 {
    interface Account { boolean withdraw(int amount); int balance(); }

    static class SyncAccount implements Account {
        private int balance;
        SyncAccount(int start) { balance = start; }
        public synchronized int balance() { return balance; }
        public synchronized boolean withdraw(int amount) {     // check AND act under one lock
            if (balance < amount) return false;
            sleep(1);
            balance -= amount;
            return true;
        }
    }

    static class AtomicButWrong implements Account {
        private final AtomicInteger bal;
        AtomicButWrong(int start) { bal = new AtomicInteger(start); }
        public int balance() { return bal.get(); }
        public boolean withdraw(int amount) {
            if (bal.get() < amount) return false;     // each call is atomic...
            sleep(1);
            bal.addAndGet(-amount);                    // ...but the pair is not
            return true;
        }
    }

    static class CasAccount implements Account {
        private final AtomicInteger bal;
        CasAccount(int start) { bal = new AtomicInteger(start); }
        public int balance() { return bal.get(); }
        public boolean withdraw(int amount) {
            while (true) {
                int old = bal.get();
                if (old < amount) return false;
                sleep(1);
                if (bal.compareAndSet(old, old - amount)) return true;   // only if nobody got there first
            }
        }
    }

    static String trial(Account acct) throws InterruptedException {
        AtomicInteger approved = new AtomicInteger();
        CountDownLatch gate = new CountDownLatch(1);
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                try { gate.await(); } catch (InterruptedException e) { return; }
                if (acct.withdraw(20)) approved.incrementAndGet();
            });
            t.start(); ts.add(t);
        }
        gate.countDown();
        for (Thread t : ts) t.join();
        return "approved " + approved.get() + ", balance " + acct.balance();
    }

    public static void main(String[] args) throws InterruptedException {
        for (int run = 1; run <= 3; run++) {
            System.out.println("run " + run + ": synchronized: " + trial(new SyncAccount(100)) + " | atomic get+add: "
                + trial(new AtomicButWrong(100)) + " | CAS loop: " + trial(new CasAccount(100)));
        }
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
