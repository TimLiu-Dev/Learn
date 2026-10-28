import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

// An at-least-once queue: a message stays "in flight" until acked, and comes back if it isn't.
public class JobQueue implements AutoCloseable {
    public record Message(long id, String body, int attempts) {}
    private record InFlight(Message message, long deadline) {}

    private final LinkedBlockingQueue<Message> ready;
    private final Map<Long, InFlight> inFlight = new ConcurrentHashMap<>();
    final List<Message> deadLetters = new CopyOnWriteArrayList<>();
    private final AtomicLong ids = new AtomicLong();
    private final long visibilityMs;
    private final int maxAttempts;
    private volatile boolean running = true;

    public JobQueue(int capacity, long visibilityMs, int maxAttempts) {
        this.ready = new LinkedBlockingQueue<>(capacity);
        this.visibilityMs = visibilityMs;
        this.maxAttempts = maxAttempts;
        Thread.ofVirtual().start(this::sweep);
    }

    public void push(String body) throws InterruptedException {             // blocks while full
        ready.put(new Message(ids.incrementAndGet(), body, 0));
    }

    public boolean offer(String body) {                                      // false while full
        return ready.offer(new Message(ids.incrementAndGet(), body, 0));
    }

    public Message receive(long timeoutMs) throws InterruptedException {
        Message m = ready.poll(timeoutMs, TimeUnit.MILLISECONDS);
        if (m == null) return null;
        Message delivery = new Message(m.id(), m.body(), m.attempts() + 1);
        inFlight.put(m.id(), new InFlight(delivery, System.currentTimeMillis() + visibilityMs));
        return delivery;
    }

    public boolean ack(long id) { return inFlight.remove(id) != null; }

    // Every 20 ms: anything in flight past its deadline goes back to the queue, or to the dead letters.
    private void sweep() {
        try {
            while (running) {
                long now = System.currentTimeMillis();
                for (var e : inFlight.entrySet()) {
                    if (e.getValue().deadline() <= now && inFlight.remove(e.getKey(), e.getValue())) {
                        Message m = e.getValue().message();
                        if (m.attempts() >= maxAttempts) deadLetters.add(m); else ready.put(m);
                    }
                }
                Thread.sleep(20);
            }
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    @Override public void close() { running = false; }
}
