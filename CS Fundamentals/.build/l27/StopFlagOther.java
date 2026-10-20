// Two more ways to make the worker see the write: an AtomicBoolean, and a lock on both sides.
import java.util.concurrent.atomic.AtomicBoolean;

public class StopFlagOther {
    static final AtomicBoolean atomicStop = new AtomicBoolean(false);

    static boolean lockedStop = false;
    static synchronized boolean isStopped() { return lockedStop; }
    static synchronized void requestStop() { lockedStop = true; }

    static void runWorker(String name, java.util.function.BooleanSupplier stopped, Runnable stop)
            throws InterruptedException {
        Thread worker = new Thread(() -> {
            long spins = 0;
            while (!stopped.getAsBoolean()) spins++;
            System.out.println(name + ": worker stopped after " + spins + " spins");
        });
        worker.setDaemon(true);
        worker.start();
        Thread.sleep(1000);
        stop.run();
        worker.join(3000);
        if (worker.isAlive()) System.out.println(name + ": still running after 3 s");
    }

    public static void main(String[] args) throws InterruptedException {
        runWorker("AtomicBoolean", atomicStop::get, () -> atomicStop.set(true));
        runWorker("synchronized ", StopFlagOther::isStopped, StopFlagOther::requestStop);
    }
}
