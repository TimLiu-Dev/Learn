import java.util.concurrent.atomic.AtomicBoolean;

// Graceful shutdown: on SIGTERM, finish the current item, then stop
public class Worker {
    static final AtomicBoolean running = new AtomicBoolean(true);   // the hook runs on another thread (L26–L27)

    public static void main(String[] args) throws InterruptedException {
        Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("hook: stop requested");
            running.set(false);
            try { mainThread.join(); } catch (InterruptedException e) { }  // wait for main to finish its item
            System.out.println("hook: main is done, exiting");
        }));
        int item = 0;
        while (running.get() && item < 100) {
            item++;
            System.out.println("item " + item + ": start");
            Thread.sleep(300);                                          // pretend to work
            System.out.println("item " + item + ": done");
        }
        System.out.println("main: stopped cleanly after " + item + " items");
    }
}
