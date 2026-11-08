public class Steal {
    public static void main(String[] args) {
        Account a = new Account(100);
        Account b = new Account(50);
        a.takeAll(b);
        System.out.println(a.balance() + " " + b.balance());
    }
}

class Account {
    private int balance;

    Account(int balance) { this.balance = balance; }

    int balance() { return balance; }

    void takeAll(Account other) {
        balance += other.balance;   // another object's private field
        other.balance = 0;
    }
}
