public class BankDemo {
    static void report(BankAccount a) {
        System.out.println(a.getOwner() + ": balance=" + a.getBalance() + " tx=" + a.getTransactionCount());
    }

    public static void main(String[] args) {
        BankAccount acct = BankAccount.withOpeningBonus("Ada", 2_000);
        report(acct);
        acct.deposit(1_000);
        acct.withdraw(700);
        report(acct);
        try {
            acct.withdraw(10_000);
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }
        try {
            acct.deposit(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }
        report(acct);
        try {
            BankAccount.open("Bob", 300);
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }
        System.out.println("minimum opening deposit: " + BankAccount.MIN_OPENING_DEPOSIT);
    }
}

final class BankAccount {
    static final long MIN_OPENING_DEPOSIT = 1_000L;   // cents
    static final long OPENING_BONUS = 500L;

    private final String owner;
    private long balance;
    private int transactionCount = 0;

    private BankAccount(String owner, long openingDeposit) {
        if (openingDeposit < MIN_OPENING_DEPOSIT) {
            throw new IllegalArgumentException("opening deposit must be at least " + MIN_OPENING_DEPOSIT);
        }
        this.owner = owner;
        this.balance = openingDeposit;
    }

    static BankAccount open(String owner, long deposit) {
        return new BankAccount(owner, deposit);
    }

    static BankAccount withOpeningBonus(String owner, long deposit) {
        return new BankAccount(owner, deposit + OPENING_BONUS);
    }

    String getOwner() { return owner; }
    long getBalance() { return balance; }
    int getTransactionCount() { return transactionCount; }

    void deposit(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("deposit must be positive");
        balance += amount;
        transactionCount++;
    }

    void withdraw(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("withdrawal must be positive");
        if (amount > balance) throw new IllegalArgumentException("insufficient funds");
        balance -= amount;
        transactionCount++;
    }
}
