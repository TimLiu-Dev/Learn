// Fix 1: always lock the account with the smaller id first. No cycle can form.
public class Ordered {
    static class Account {
        final int id;
        long balance = 1_000_000;
        Account(int id) { this.id = id; }
    }

    static void transfer(Account from, Account to, long amount) {
        Account first = from.id < to.id ? from : to;    // a global order: by id
        Account second = from.id < to.id ? to : from;
        synchronized (first) {
            synchronized (second) {
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Account alice = new Account(1), bob = new Account(2);
        Thread t1 = new Thread(() -> { for (int i = 0; i < 1_000_000; i++) transfer(alice, bob, 1); });
        Thread t2 = new Thread(() -> { for (int i = 0; i < 1_000_000; i++) transfer(bob, alice, 1); });
        long start = System.nanoTime();
        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.printf("2,000,000 opposite transfers in %d ms%n", (System.nanoTime() - start) / 1_000_000);
        System.out.println("alice=" + alice.balance + " bob=" + bob.balance
                + " total=" + (alice.balance + bob.balance));
    }
}
