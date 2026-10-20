import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;

// Nearest equivalent: StructuredTaskScope, a PREVIEW API in JDK 26 (JEP 525).
// Run with:  java --enable-preview --source 26 Structured.java
public class Structured {
    static String task(String name, long ms, boolean fail) throws InterruptedException {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.out.println("  " + name + " cancelled");
            throw e;
        }
        if (fail) throw new IllegalStateException(name + " failed");
        System.out.println("  " + name + " finished");
        return name;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("1. join() waits for every subtask");
        try (var scope = StructuredTaskScope.open()) {
            scope.fork(() -> task("A", 100, false));
            scope.fork(() -> task("B", 200, false));
            scope.join();
        }
        System.out.println("   scope returned after both");

        System.out.println("2. one subtask fails: the others are cancelled");
        try (var scope = StructuredTaskScope.open()) {
            scope.fork(() -> task("C", 300, false));
            scope.fork(() -> task("D", 100, true));
            scope.join();
        } catch (StructuredTaskScope.FailedException e) {
            System.out.println("   caught: " + e.getCause().getMessage());
        }

        System.out.println("3. Joiner.awaitAll(): a failure stays local");
        try (var scope = StructuredTaskScope.open(Joiner.<String>awaitAll())) {
            Subtask<String> e = scope.fork(() -> task("E", 300, false));
            Subtask<String> f = scope.fork(() -> task("F", 100, true));
            scope.join();
            System.out.println("   E -> " + e.get());
            System.out.println("   F -> " + f.exception().getMessage());
        }
    }
}
