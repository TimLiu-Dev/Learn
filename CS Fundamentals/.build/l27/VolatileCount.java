// volatile makes every write visible, but count++ is still read, add, write.
public class VolatileCount {
    static volatile int count = 0;

    public static void main(String[] args) throws InterruptedException {
        for (int run = 1; run <= 5; run++) {
            count = 0;
            Thread[] threads = new Thread[4];
            for (int t = 0; t < 4; t++) {
                threads[t] = new Thread(() -> {
                    for (int i = 0; i < 1_000_000; i++) count++;
                });
                threads[t].start();
            }
            for (Thread t : threads) t.join();
            System.out.println("run " + run + ": " + count + " (expected 4000000)");
        }
    }
}
