import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PoolBasics {
    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        try (ExecutorService pool = Executors.newFixedThreadPool(3)) {   // close() waits
            List<Future<String>> futures = new ArrayList<>();
            for (int id = 1; id <= 6; id++) {
                int taskId = id;                    // lambdas need an effectively final copy
                futures.add(pool.submit(() -> {
                    Thread.sleep(100);              // pretend to do 100 ms of I/O
                    return "task " + taskId + " ran on " + Thread.currentThread().getName();
                }));
            }
            for (Future<String> f : futures) System.out.println(f.get());
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println("pool closed after " + ms + " ms");
    }
}
