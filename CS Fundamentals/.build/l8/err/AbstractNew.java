public class AbstractNew {
    static abstract class Account {
        abstract long monthlyFee();
    }

    public static void main(String[] args) {
        Account a = new Account();
    }
}
