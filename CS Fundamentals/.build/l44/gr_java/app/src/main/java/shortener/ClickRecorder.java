package shortener;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/** The analytics queue (L42): redirects only enqueue; a background flusher batches the writes. */
public class ClickRecorder {
    private final LinkStore store;
    private final Clock clock;
    private final LinkedBlockingQueue<String> queue;
    public final AtomicLong dropped = new AtomicLong();
    private final Thread worker;

    public ClickRecorder(LinkStore store, Clock clock) { this(store, clock, 100_000); }

    public ClickRecorder(LinkStore store, Clock clock, int capacity) {
        this.store = store;
        this.clock = clock;
        this.queue = new LinkedBlockingQueue<>(capacity);
        this.worker = Thread.ofVirtual().name("click-flusher").start(() -> {
            try {
                while (true) {
                    Thread.sleep(200);
                    flush();
                }
            } catch (InterruptedException e) {
                // stop() asked us to finish
            }
        });
    }

    /** Never blocks a redirect: if the queue is full, the click is dropped and counted. */
    public void record(String code) {
        if (!queue.offer(code)) dropped.incrementAndGet();
    }

    /** Drain and write everything queued so far, in one transaction. One flush at a time. */
    public synchronized void flush() {
        List<String> codes = new ArrayList<>();
        queue.drainTo(codes);
        if (codes.isEmpty()) return;
        Map<String, Integer> counts = codes.stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(c -> 1)));
        try {
            store.addClicks(LocalDate.now(clock).toString(), counts);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void stop() throws InterruptedException {
        worker.interrupt();
        worker.join();
        flush();
    }
}
