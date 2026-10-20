import java.util.concurrent.*;

// Nearest equivalents: a future (like async) and a thread (like launch)
public class Exceptions {
    public static void main(String[] args) throws Exception {
        // CompletableFuture: the exception waits until join()/get()
        var f = CompletableFuture.supplyAsync(() -> {
            throw new IllegalArgumentException("bad input");
        });
        Thread.sleep(100);
        System.out.println("future failed quietly so far: isDone=" + f.isDone());
        try {
            f.join();
        } catch (CompletionException e) {
            System.out.println("join threw: " + e.getCause().getMessage());
        }

        // A thread: the exception goes to its uncaught-exception handler
        Thread t = Thread.ofVirtual()
            .uncaughtExceptionHandler((th, e) ->
                System.out.println("handler got: " + e.getMessage()))
            .unstarted(() -> { throw new IllegalStateException("thread blew up"); });
        t.start();
        t.join();
        System.out.println("main still running");
    }
}
