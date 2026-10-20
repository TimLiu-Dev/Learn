import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Bounded {
    static void log(String msg) {
        System.out.println("  [" + Thread.currentThread().getName() + "] " + msg);
    }

    static void run(RejectedExecutionHandler policy) throws InterruptedException {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
            2, 2,                              // core and max: 2 workers
            0L, TimeUnit.MILLISECONDS,         // keep-alive (unused when core == max)
            new ArrayBlockingQueue<>(2),       // bounded queue: at most 2 waiting tasks
            policy);
        for (int i = 1; i <= 6; i++) {
            int id = i;
            try {
                pool.execute(() -> {
                    try { Thread.sleep(300); } catch (InterruptedException e) { return; }
                    log("finished task " + id);
                });
                System.out.println("submitted task " + id + ", queue size " + pool.getQueue().size());
            } catch (RejectedExecutionException e) {
                System.out.println("REJECTED task " + id);
                if (id == 5) System.out.println("  " + e.getMessage());
            }
        }
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("== AbortPolicy (the default)");
        run(new ThreadPoolExecutor.AbortPolicy());
        System.out.println("== CallerRunsPolicy");
        run(new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
