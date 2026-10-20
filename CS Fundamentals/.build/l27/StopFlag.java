// A worker spins until main sets stop = true. Without volatile, may it never see the write?
public class StopFlag {
    static boolean stop = false;           // plain field: no visibility guarantee

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            long spins = 0;
            while (!stop) {
                spins++;                   // busy loop, no synchronization inside
            }
            System.out.println("worker saw stop after " + spins + " spins");
        });
        worker.setDaemon(true);            // a stuck daemon can't keep the JVM alive
        worker.start();

        Thread.sleep(1000);                // let the loop run hot enough to be JIT-compiled
        stop = true;
        System.out.println("main set stop = true");

        worker.join(3000);                 // wait at most 3 s
        if (worker.isAlive()) {
            System.out.println("still running after 3 s: the worker never saw the write");
        }
    }
}
