import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

class Account {
    private long balance;
    Account(long balance) { this.balance = balance; }
    public void deposit(int cents) { balance += cents; }
    private String audit() { return "balance=" + balance; }
}

public class Inspect {
    public static void main(String[] args) throws Exception {
        Class<Account> k = Account.class;
        System.out.println("fields:  " + Arrays.stream(k.getDeclaredFields()).map(Field::getName).sorted().toList());
        System.out.println("methods: " + Arrays.stream(k.getDeclaredMethods()).map(Method::getName).sorted().toList());

        Account acct = new Account(1_000);
        // Look a method up by name + parameter types, then call it.
        Method deposit = k.getMethod("deposit", int.class);
        deposit.invoke(acct, 250);

        // Private members: allowed only after switching off access checks.
        Method audit = k.getDeclaredMethod("audit");
        audit.setAccessible(true);
        System.out.println(audit.invoke(acct));

        Field field = k.getDeclaredField("balance");
        field.setAccessible(true);
        field.setLong(acct, 5);                       // bypasses every rule you wrote
        System.out.println(audit.invoke(acct));
    }
}
