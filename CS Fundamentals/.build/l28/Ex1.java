import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Ex1 {
    public static void main(String[] args) throws InterruptedException {
        AtomicInteger rejected = new AtomicInteger();
        RejectedExecutionHandler countingHandler = (task, executor) -> rejected.incrementAndGet();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
            2, 4, 1, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2),
            Thread.ofPlatform().name("worker-", 1).factory(),
            countingHandler);
        for (int i = 1; i <= 10; i++) {
            int id = i;
            pool.execute(() -> {
                System.out.println("task " + id + " on " + Thread.currentThread().getName());
                try { Thread.sleep(200); } catch (InterruptedException e) { }
            });
        }
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("largest pool size: " + pool.getLargestPoolSize());
        System.out.println("completed: " + pool.getCompletedTaskCount() + ", rejected: " + rejected.get());
    }
}
