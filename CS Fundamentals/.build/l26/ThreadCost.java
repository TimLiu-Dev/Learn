import java.util.ArrayList;
import java.util.List;

public class ThreadCost {
    public static void main(String[] args) throws InterruptedException {
        int n = 10_000;
        for (int round = 1; round <= 3; round++) {
            long start = System.nanoTime();
            List<Thread> threads = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                Thread t = new Thread(() -> { });    // start 10,000 threads that do nothing
                t.start();
                threads.add(t);
            }
            for (Thread t : threads) t.join();
            long ms = (System.nanoTime() - start) / 1_000_000;
            System.out.println("round " + round + ": " + n + " threads started and joined in " + ms + " ms (" + (ms * 1000 / n) + " µs each)");
        }
    }
}
