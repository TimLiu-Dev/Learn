import java.util.concurrent.*;

// Nearest equivalent: choose an executor yourself for each task
public class Dispatch {
    static void where(String label) {
        System.out.printf("%-26s %s%n", label, Thread.currentThread());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("cores: " + Runtime.getRuntime().availableProcessors());
        where("main:");
        CompletableFuture.runAsync(() -> where("common pool (CPU):")).join();
        try (var io = Executors.newCachedThreadPool()) {
            CompletableFuture.runAsync(() -> where("cached pool (IO):"), io).join();
        }
        Thread.ofVirtual().start(() -> {
            where("virtual, before sleep:");
            sleep(10);
            where("virtual, after sleep:");
        }).join();
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
