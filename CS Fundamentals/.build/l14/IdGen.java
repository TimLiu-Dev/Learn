import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class IdGen {
    static Supplier<String> idGenerator(String prefix) {
        AtomicInteger next = new AtomicInteger(1);   // the reference never changes
        return () -> prefix + "-" + next.getAndIncrement();
    }

    public static void main(String[] args) {
        Supplier<String> orders = idGenerator("ORD");
        Supplier<String> users = idGenerator("USR");
        System.out.println(orders.get());
        System.out.println(orders.get());
        System.out.println(users.get());
        System.out.println(orders.get());
    }
}
