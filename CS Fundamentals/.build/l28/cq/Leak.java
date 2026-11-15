import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Leak {
    public static void main(String[] args) {
        Semaphore permits = new Semaphore(10);
        try (var ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) {
                int id = i;
                ex.submit(() -> {
                    permits.acquire();
                    if (id % 2 == 0) throw new IllegalStateException("query " + id + " failed");
                    permits.release();
                    return id;
                });
            }
        }
        System.out.println(permits.availablePermits());
    }
}
