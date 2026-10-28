package gateway;

import java.time.Instant;
import java.time.InstantSource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenBucketTest {
    private final AtomicLong now = new AtomicLong(0);
    private final InstantSource clock = () -> Instant.ofEpochMilli(now.get());

    @Test void aBurstUpToCapacityPassesTheNextIsRejected() {
        TokenBucket bucket = new TokenBucket(10.0, 10.0, clock);
        for (int i = 0; i < 10; i++) assertTrue(bucket.tryAcquire());
        assertFalse(bucket.tryAcquire());
    }

    @Test void tokensComeBackAsTimePasses() {
        TokenBucket bucket = new TokenBucket(10.0, 10.0, clock);
        for (int i = 0; i < 10; i++) bucket.tryAcquire();
        now.addAndGet(250);                                                   // 2.5 tokens
        int granted = 0;
        for (int i = 0; i < 5; i++) if (bucket.tryAcquire()) granted++;
        assertEquals(2, granted);
    }

    @Test void retryAfterIsAtLeastOneWholeSecond() {
        TokenBucket bucket = new TokenBucket(1.0, 0.5, clock);
        bucket.tryAcquire();
        assertEquals(2, bucket.retryAfterSeconds());                          // one token every 2 s
    }

    @Test void eightThreadsNeverGetMoreThanCapacity() throws Exception {
        TokenBucket bucket = new TokenBucket(100.0, 10.0, clock);             // the clock never moves: no refill
        AtomicInteger granted = new AtomicInteger();
        CountDownLatch go = new CountDownLatch(1);
        try (ExecutorService pool = Executors.newFixedThreadPool(8)) {
            for (int t = 0; t < 8; t++) pool.submit(() -> { go.await(); for (int i = 0; i < 50; i++) if (bucket.tryAcquire()) granted.incrementAndGet(); return null; });
            go.countDown();
        }
        assertEquals(100, granted.get());                                     // 400 tries, exactly 100 tokens
    }
}
