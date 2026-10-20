import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Ex2 {
    static class Account {
        int balance;
        Account(int balance) { this.balance = balance; }

        boolean withdraw(int amount) throws InterruptedException {
            if (balance >= amount) {             // check...
                Thread.sleep(1);                 // (a slow fraud check widens the window)
                balance -= amount;               // ...then act
                return true;
            }
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int run = 1; run <= 3; run++) {
            Account acct = new Account(100);
            AtomicInteger approved = new AtomicInteger();
            CountDownLatch gate = new CountDownLatch(1);
            List<Thread> customers = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Thread t = new Thread(() -> {
                    try {
                        gate.await();
                        if (acct.withdraw(20)) approved.incrementAndGet();
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                });
                t.start(); customers.add(t);
            }
            gate.countDown();
            for (Thread t : customers) t.join();
            System.out.println("run " + run + ": approved " + approved.get() + " withdrawals of 20, balance = " + acct.balance);
        }
    }
}
