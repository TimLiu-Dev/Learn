import java.util.List;

public class Withdraw {
    static class OverdraftException extends Exception {          // checked: recoverable
        OverdraftException(long requested, long available) {
            super("cannot withdraw " + requested + ", only " + available + " available");
        }
    }

    static class Account {
        private long balance;
        Account(long balance) { this.balance = balance; }

        void withdraw(long amount) throws OverdraftException {
            if (amount <= 0) throw new IllegalArgumentException("amount must be positive, was " + amount);
            if (amount > balance) throw new OverdraftException(amount, balance);
            balance -= amount;
        }

        long balance() { return balance; }
    }

    public static void main(String[] args) {
        Account acct = new Account(1_000);
        for (long amount : List.of(400L, 900L, -5L)) {
            try {
                acct.withdraw(amount);
                System.out.println("withdrew " + amount + ", balance " + acct.balance());
            } catch (OverdraftException e) {
                System.out.println("declined: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("bug in caller: " + e.getMessage());
            }
        }
    }
}
