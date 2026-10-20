import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForgotShutdown {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.submit(() -> System.out.println("task ran on " + Thread.currentThread().getName()));
        System.out.println("main is done");
        // forgot pool.shutdown() / pool.close()
    }
}
