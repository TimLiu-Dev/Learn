import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureGet {
    static Callable<String> slow(String name, long ms) {
        return () -> {
            Thread.sleep(ms);
            return name + " after " + ms + " ms";
        };
    }

    public static void main(String[] args) throws Exception {
        try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
            // get with a timeout
            Future<String> f = pool.submit(slow("report", 500));
            try {
                System.out.println(f.get(100, TimeUnit.MILLISECONDS));
            } catch (TimeoutException e) {
                System.out.println("gave up waiting after 100 ms; cancel = " + f.cancel(true));
            }

            // invokeAll: wait for every task; results come back in submission order
            long start = System.nanoTime();
            List<Future<String>> all = pool.invokeAll(List.of(slow("a", 300), slow("b", 100), slow("c", 200)));
            List<String> results = new ArrayList<>();
            for (Future<String> r : all) results.add(r.get());
            System.out.println("invokeAll: " + results + " in " + (System.nanoTime() - start) / 1_000_000 + " ms");

            // invokeAny: the first one to finish successfully wins; the rest are cancelled
            start = System.nanoTime();
            String first = pool.invokeAny(List.of(slow("mirror-1", 300), slow("mirror-2", 100), slow("mirror-3", 200)));
            System.out.println("invokeAny: " + first + " in " + (System.nanoTime() - start) / 1_000_000 + " ms");
        }
    }
}
