package gateway;

import java.util.Random;
import java.util.function.Predicate;

/** Capped exponential backoff with full jitter. Only retryable failures are retried. */
public final class Retry {
    private Retry() {}

    public interface Attempt<T> { T run(int attempt) throws Exception; }

    public static <T> T retry(int maxAttempts, long baseMs, long capMs, Random random,
                              Predicate<Exception> retryable, Attempt<T> block) throws Exception {
        for (int attempt = 1; ; attempt++) {
            try {
                return block.run(attempt);
            } catch (Exception e) {
                if (!retryable.test(e) || attempt == maxAttempts) throw e;
                long backoff = Math.min(capMs, baseMs << (attempt - 1));
                double r; synchronized (random) { r = random.nextDouble(); }
                Thread.sleep((long) (r * backoff));
            }
        }
    }
}
