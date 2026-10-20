import java.util.List;

public class Abstract {
    static abstract class Account {                 // shared state + constructor
        private final String owner;
        private long balance;

        Account(String owner, long balance) {
            this.owner = owner;
            this.balance = balance;
        }

        long getBalance() { return balance; }

        protected void setBalance(long balance) { this.balance = balance; }   // subclasses may change it

        abstract long monthlyFee();                 // each kind of account decides

        void applyFee() {                           // shared logic using the abstract part
            balance -= monthlyFee();
        }

        @Override
        public String toString() { return owner + ": " + balance; }
    }

    static class Checking extends Account {
        Checking(String owner, long balance) { super(owner, balance); }
        @Override
        long monthlyFee() { return getBalance() < 1000 ? 5 : 0; }
    }

    static class Savings extends Account {
        Savings(String owner, long balance) { super(owner, balance); }
        @Override
        long monthlyFee() { return 0; }
    }

    public static void main(String[] args) {
        List<Account> accounts = List.of(new Checking("Ada", 500), new Checking("Grace", 5000), new Savings("Linus", 2000));
        for (Account a : accounts) a.applyFee();
        System.out.println(accounts);
    }
}
