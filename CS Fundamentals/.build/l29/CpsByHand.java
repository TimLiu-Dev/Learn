import java.util.concurrent.*;
import java.util.function.Consumer;

// What `suspend` saves you from writing: pass "the rest
// of the function" along as a callback (a continuation).
public class CpsByHand {
    static final ScheduledExecutorService timer =
        Executors.newSingleThreadScheduledExecutor();

    static void delay(long ms, Runnable then) {
        timer.schedule(then, ms, TimeUnit.MILLISECONDS);
    }

    static void fetchUser(int id, Consumer<String> continuation) {
        delay(100, () -> {                    // resumes here: step 2
            String name = "user" + id;
            delay(100, () ->                  // resumes here: step 3
                continuation.accept(name.toUpperCase()));
        });
    }

    public static void main(String[] args) {
        fetchUser(7, result -> {
            System.out.println(result);
            timer.shutdown();
        });
    }
}
