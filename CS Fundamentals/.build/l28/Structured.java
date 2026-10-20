import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

// PREVIEW API in JDK 26 (JEP 525). Run with: java --enable-preview Structured.java
public class Structured {
    static String findUser() throws InterruptedException { Thread.sleep(200); return "Ada"; }
    static int fetchOrder(boolean fail) throws InterruptedException {
        Thread.sleep(100);
        if (fail) throw new IllegalStateException("order service down");
        return 7;
    }

    static String handle(boolean fail) throws InterruptedException {
        try (var scope = StructuredTaskScope.open()) {        // each fork runs in a new virtual thread
            Subtask<String> user = scope.fork(() -> findUser());
            Subtask<Integer> order = scope.fork(() -> fetchOrder(fail));
            scope.join();                                     // waits for both; throws if either failed
            return user.get() + " has order #" + order.get();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.nanoTime();
        System.out.println(handle(false) + " (" + (System.nanoTime() - start) / 1_000_000 + " ms)");
        start = System.nanoTime();
        try {
            handle(true);
        } catch (StructuredTaskScope.FailedException e) {
            System.out.println("failed after " + (System.nanoTime() - start) / 1_000_000 + " ms: " + e.getCause());
        }
    }
}
