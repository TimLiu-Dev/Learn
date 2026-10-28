import java.io.IOException;
import java.util.Random;
import java.util.function.Predicate;

// Exercise solution: retry with capped exponential backoff and full jitter.
// Only exceptions that `retryable` accepts are retried; anything else fails at once.
public class Retry {
    interface Attempt<T> { T run(int attempt) throws Exception; }

    static <T> T retry(int maxAttempts, long baseMs, long capMs, Random random,
                       Predicate<Exception> retryable, Attempt<T> block) throws Exception {
        for (int attempt = 1; ; attempt++) {
            try {
                return block.run(attempt);
            } catch (Exception e) {
                if (!retryable.test(e) || attempt == maxAttempts) throw e;
                long backoff = Math.min(capMs, baseMs << (attempt - 1));
                long sleep = (long) (random.nextDouble() * backoff);         // full jitter: 0..backoff
                System.out.println("  attempt " + attempt + " failed (" + e.getMessage() + "); sleeping " + sleep + " ms of up to " + backoff);
                Thread.sleep(sleep);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Random random = new Random(43);
        Predicate<Exception> ioOnly = e -> e instanceof IOException;
        System.out.println("flaky GET, fails twice:");
        String price = retry(4, 100, 1000, random, ioOnly, n -> { if (n <= 2) throw new IOException("503"); return "12.99"; });
        System.out.println("  -> " + price);

        System.out.println("bad request, not retryable:");
        try { retry(4, 100, 1000, random, ioOnly, n -> { throw new IllegalArgumentException("400 bad id"); }); }
        catch (IllegalArgumentException e) { System.out.println("  -> gave up at once: " + e.getMessage()); }

        System.out.println("always down, 4 attempts:");
        try { retry(4, 100, 1000, random, ioOnly, n -> { throw new IOException("connection refused"); }); }
        catch (IOException e) { System.out.println("  -> gave up: " + e.getMessage()); }
    }
}
