import java.util.concurrent.*;

public class CfCancel {
    public static void main(String[] args) throws Exception {
        CountDownLatch started = new CountDownLatch(1), go = new CountDownLatch(1), finished = new CountDownLatch(1);
        CompletableFuture<String> task = CompletableFuture.supplyAsync(() -> {
            started.countDown();
            String how;
            try {
                go.await();                      // wait until main says go
                how = "ran";
            } catch (InterruptedException e) {
                how = "interrupted";
            }
            System.out.print(how + " ");
            finished.countDown();
            return how;
        });
        started.await();
        boolean cancelled = task.cancel(true);   // true = "may interrupt if running"
        go.countDown();
        finished.await();
        System.out.println(cancelled + " " + task.isCancelled());
    }
}
