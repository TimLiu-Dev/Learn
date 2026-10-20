// One producer, two consumers, a bounded queue of 5, and a poison pill to shut down.
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class Pipeline {
    static final int POISON = -1;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
        AtomicLong total = new AtomicLong();

        Runnable consumer = () -> {
            int taken = 0;
            try {
                while (true) {
                    int job = queue.take();             // blocks while the queue is empty
                    if (job == POISON) break;
                    total.addAndGet(job);
                    taken++;
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.println(Thread.currentThread().getName() + " took " + taken + " jobs");
        };
        Thread c1 = new Thread(consumer, "consumer-1");
        Thread c2 = new Thread(consumer, "consumer-2");
        c1.start(); c2.start();

        for (int job = 1; job <= 1000; job++) queue.put(job); // blocks while the queue is full
        queue.put(POISON);                                     // one pill per consumer
        queue.put(POISON);

        c1.join(); c2.join();
        System.out.println("total " + total.get() + " (expected 500500)");
    }
}
