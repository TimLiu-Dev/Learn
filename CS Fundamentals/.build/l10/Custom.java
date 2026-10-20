public class Custom {
    // extends Exception (not RuntimeException), so it is a CHECKED exception
    static class InsufficientFundsException extends Exception {
        private final long shortByCents;

        InsufficientFundsException(long shortByCents) {
            super("insufficient funds: short by " + shortByCents + " cents");
            this.shortByCents = shortByCents;
        }

        long shortByCents() { return shortByCents; }
    }

    static class Wallet {
        private long cents;
        Wallet(long cents) { this.cents = cents; }

        void pay(long amount) throws InsufficientFundsException {   // must be declared
            if (amount > cents) throw new InsufficientFundsException(amount - cents);
            cents -= amount;
        }
    }

    public static void main(String[] args) {
        Wallet w = new Wallet(500);
        try {
            w.pay(300);
            w.pay(450);
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage() + " (shortBy=" + e.shortByCents() + ")");
        }
    }
}
