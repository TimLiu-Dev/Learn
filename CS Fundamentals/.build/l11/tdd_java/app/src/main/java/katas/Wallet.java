package katas;

public class Wallet {
    private long balance = 0;

    public long getBalance() {
        return balance;
    }

    public void deposit(long cents) {
        if (cents <= 0) throw new IllegalArgumentException("deposit must be positive");
        balance += cents;
    }

    public void withdraw(long cents) {
        if (cents > balance) throw new IllegalArgumentException("insufficient funds");
        balance -= cents;
    }
}
