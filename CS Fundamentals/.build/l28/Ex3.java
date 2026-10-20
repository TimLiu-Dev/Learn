import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

public class Ex3 {
    public static void main(String[] args) throws InterruptedException {
        LongAdder done = new LongAdder();                    // many writers: L26's best counter
        Thread.Builder builder = Thread.ofVirtual().name("v-", 0);   // v-0, v-1, v-2, ...
        long start = System.nanoTime();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 100_000; i++) {
            threads.add(builder.start(() -> {
                try { Thread.sleep(100); } catch (InterruptedException e) { return; }
                done.increment();
            }));
        }
        for (Thread t : threads) t.join();
        long ms = (System.nanoTime() - start) / 1_000_000;
        Thread last = threads.get(threads.size() - 1);
        System.out.println(done.sum() + " finished in " + ms + " ms; last = " + last.getName()
                + ", virtual = " + last.isVirtual() + ", daemon = " + last.isDaemon());
    }
}
