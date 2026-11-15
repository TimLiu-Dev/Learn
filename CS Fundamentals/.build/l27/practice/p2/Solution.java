import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Practice {
    // Every lock is taken through locked(), so the checks can watch the order.
    static volatile Consumer<Account> onLock = acc -> { };

    static final class Account {
        final int id;
        long balance;
        Account(int id, long balance) { this.id = id; this.balance = balance; }

        <T> T locked(Supplier<T> body) {
            synchronized (this) {
                onLock.accept(this);
                return body.get();
            }
        }
    }

    static boolean transfer(Account from, Account to, long amount) {
        Account first = from.id < to.id ? from : to;      // one global order: by id
        Account second = first == from ? to : from;
        return first.locked(() -> second.locked(() -> {
            if (from.balance < amount) return false;
            from.balance -= amount;
            to.balance += amount;
            return true;
        }));
    }

    static long audit(List<Account> accounts) {
        List<Account> sorted = new ArrayList<>(accounts);
        sorted.sort(Comparator.comparingInt(a -> a.id));
        return lockFrom(sorted, 0);
    }

    // Take lock i, and while holding it, take the rest; sum only when all are held.
    static long lockFrom(List<Account> sorted, int i) {
        if (i == sorted.size()) return sorted.stream().mapToLong(a -> a.balance).sum();
        return sorted.get(i).locked(() -> lockFrom(sorted, i + 1));
    }

    // CHECKS
    public static void main(String[] args) {
        check("money moves, and an overdraft is refused", "true false 40 60", () -> {
            var a = new Account(1, 100); var b = new Account(2, 0);
            boolean ok = transfer(a, b, 60), refused = transfer(a, b, 50);
            return ok + " " + refused + " " + a.balance + " " + b.balance;
        });
        check("transfer(a, b) locks the lower id first", List.of(1, 2), () -> { var a = new Account(1, 100); var b = new Account(2, 100); return locksTakenBy(() -> transfer(a, b, 10)); });
        check("transfer(b, a) locks the lower id first", List.of(1, 2), () -> { var a = new Account(1, 100); var b = new Account(2, 100); return locksTakenBy(() -> transfer(b, a, 10)); });
        check("opposite transfers can't deadlock, even in the worst interleaving", "both finished, a=110 b=90", Practice::worstInterleaving);
        check("audit locks every account in id order, all held at once", "order [1, 2, 3], held [1, 2, 3], total 60", () -> {
            var accounts = List.of(new Account(3, 30), new Account(1, 10), new Account(2, 20));
            List<Integer> held = new ArrayList<>();
            long[] total = new long[1];
            List<Integer> order = locksTakenBy(() -> {
                Consumer<Account> record = onLock;
                onLock = acc -> { record.accept(acc); held.add((int) accounts.stream().filter(Thread::holdsLock).count()); };
                total[0] = audit(accounts);
            });
            return "order " + order + ", held " + held + ", total " + total[0];
        });
    }

    static List<Integer> locksTakenBy(Runnable action) {
        List<Integer> ids = new ArrayList<>();
        onLock = acc -> ids.add(acc.id);
        try { action.run(); } finally { onLock = acc -> { }; }
        return ids;
    }

    static String worstInterleaving() throws Exception {
        var a = new Account(1, 100); var b = new Account(2, 100);
        Thread[] t = new Thread[2];
        Set<Thread> started = ConcurrentHashMap.newKeySet();
        AtomicInteger holding = new AtomicInteger();
        // After its FIRST lock, each thread waits until the other one holds a lock too,
        // or is blocked waiting for one. That forces the deadly interleaving if it's possible.
        onLock = acc -> {
            if (started.add(Thread.currentThread())) {
                holding.incrementAndGet();
                Thread other = Thread.currentThread() == t[0] ? t[1] : t[0];
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
                while (holding.get() < 2 && other.getState() != Thread.State.BLOCKED
                        && System.nanoTime() < deadline) Thread.onSpinWait();
            }
        };
        t[0] = new Thread(() -> transfer(a, b, 10));
        t[1] = new Thread(() -> transfer(b, a, 20));
        for (Thread th : t) { th.setDaemon(true); th.start(); }
        long end = System.currentTimeMillis() + 2000;           // 2 s in all, not 2 s per thread
        for (Thread th : t) th.join(Math.max(1, end - System.currentTimeMillis()));
        onLock = acc -> { };
        if (t[0].isAlive() || t[1].isAlive()) return "DEADLOCK: each thread holds one account and waits for the other";
        return "both finished, a=" + a.balance + " b=" + b.balance;
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
