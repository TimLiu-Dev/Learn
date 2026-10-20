import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LostException {
    public static void main(String[] args) throws InterruptedException {
        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {
            // 1. submit: the exception is stored in the Future... which nobody reads
            pool.submit(() -> { throw new IllegalStateException("boom in submit"); });

            // 2. execute: no Future, so the exception reaches the thread's uncaught handler
            pool.execute(() -> { throw new IllegalStateException("boom in execute"); });
            Thread.sleep(200);

            // 3. submit + get: the exception comes back, wrapped
            Future<Integer> f = pool.submit(() -> {
                throw new IllegalStateException("boom, kept in the Future");
            });
            try {
                f.get();
            } catch (ExecutionException e) {
                System.out.println("caught " + e.getClass().getSimpleName());
                System.out.println("  cause: " + e.getCause());
            }
        }
    }
}
