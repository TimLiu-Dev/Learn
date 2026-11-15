import java.util.*;

public class Practice {
    // One row of the accounts table: its balance and its version number.
    static class Account {
        int balance, version;
        Account(int balance, int version) { this.balance = balance; this.version = version; }

        // UPDATE accounts SET balance_cents = ?, version = version + 1 WHERE id = 1 AND version = ?
        // Returns the update count, like executeUpdate: 1 if the row matched, 0 if it didn't.
        int updateIfVersion(int newBalance, int expectedVersion) {
            if (version != expectedVersion) return 0;
            balance = newBalance;
            version++; // every successful write makes all older reads stale
            return 1;
        }
    }

    // A deposit transaction: read the row, then write balance + amount.
    static class Deposit {
        final int amount;
        int seenBalance, seenVersion;
        Deposit(int amount) { this.amount = amount; }

        void read(Account acc) {
            seenBalance = acc.balance;
            seenVersion = acc.version;
        }

        // The lost-update bug: writes what it computed, whatever happened since the read.
        boolean writeBlindly(Account acc) {
            acc.balance = seenBalance + amount;
            return true;
        }

        // Optimistic locking: true if the write went through, false if someone got there first.
        boolean writeIfUnchanged(Account acc) {
            return acc.updateIfVersion(seenBalance + amount, seenVersion) == 1;
        }
    }

    // Runs steps like "T1 read" and "T2 write" in exactly this order, on one thread.
    // Returns how many writes had to be retried.
    static int runSchedule(Account acc, Map<String, Deposit> txs, List<String> steps, boolean optimistic) {
        Deque<String> queue = new ArrayDeque<>(steps);
        int retries = 0;
        while (!queue.isEmpty()) {
            String[] step = queue.removeFirst().split(" ");
            Deposit tx = txs.get(step[0]);
            if (step[1].equals("read")) {
                tx.read(acc);
            } else {
                boolean ok = optimistic ? tx.writeIfUnchanged(acc) : tx.writeBlindly(acc);
                if (!ok) { // stale read: start this transaction over
                    retries++;
                    queue.addLast(step[0] + " read");
                    queue.addLast(step[0] + " write");
                }
            }
        }
        return retries;
    }

    // CHECKS
    static final List<String> INTERLEAVED = List.of("T1 read", "T2 read", "T1 write", "T2 write");

    static Map<String, Deposit> two() {
        Map<String, Deposit> txs = new LinkedHashMap<>();
        txs.put("T1", new Deposit(1000));
        txs.put("T2", new Deposit(1000));
        return txs;
    }

    public static void main(String[] args) {
        check("a blind write loses the first deposit", 11000, () -> {
            Account acc = new Account(10000, 0);
            runSchedule(acc, two(), INTERLEAVED, false);
            return acc.balance;
        });
        check("a matching version updates the row and bumps the version", "count 1, balance 10500, version 8", () -> {
            Account acc = new Account(10000, 7);
            int n = acc.updateIfVersion(10500, 7);
            return "count " + n + ", balance " + acc.balance + ", version " + acc.version;
        });
        check("a stale version changes nothing", "count 0, balance 10000, version 8", () -> {
            Account acc = new Account(10000, 8);
            int n = acc.updateIfVersion(10500, 7);
            return "count " + n + ", balance " + acc.balance + ", version " + acc.version;
        });
        check("optimistic writes keep both deposits", "balance 12000, retries 1", () -> {
            Account acc = new Account(10000, 0);
            int retries = runSchedule(acc, two(), INTERLEAVED, true);
            return "balance " + acc.balance + ", retries " + retries;
        });
        check("a serial schedule needs no retries", "balance 12000, retries 0", () -> {
            Account acc = new Account(10000, 0);
            int retries = runSchedule(acc, two(), List.of("T1 read", "T1 write", "T2 read", "T2 write"), true);
            return "balance " + acc.balance + ", retries " + retries;
        });
        check("three readers, then three writers", "balance 111, retries 2", () -> {
            Account acc = new Account(0, 0);
            Map<String, Deposit> txs = new LinkedHashMap<>();
            txs.put("T1", new Deposit(1));
            txs.put("T2", new Deposit(10));
            txs.put("T3", new Deposit(100));
            int retries = runSchedule(acc, txs, List.of("T1 read", "T2 read", "T3 read", "T1 write", "T2 write", "T3 write"), true);
            return "balance " + acc.balance + ", retries " + retries;
        });
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
