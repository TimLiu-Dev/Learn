import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class CheckThenAct {
    static final AtomicInteger created = new AtomicInteger();   // counts constructor calls safely

    static class Config {
        Config() {
            created.incrementAndGet();
            sleep(5);                                  // "expensive" setup widens the race window
        }
    }

    static Config instance;
    static Config getConfig() {
        if (instance == null) {                        // check...
            instance = new Config();                   // ...then act: another thread may be here too
        }
        return instance;
    }

    public static void main(String[] args) throws InterruptedException {
        for (int run = 1; run <= 5; run++) {
            created.set(0); instance = null;
            hammer(CheckThenAct::getConfig);
            System.out.println("run " + run + ": Config created " + created.get() + " times by getConfig()");
        }
    }

    static void hammer(Runnable task) throws InterruptedException {
        CountDownLatch gate = new CountDownLatch(1);   // hold all 8 threads at the start line
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Thread t = new Thread(() -> {
                try { gate.await(); } catch (InterruptedException e) { return; }
                task.run();
            });
            t.start();
            threads.add(t);
        }
        gate.countDown();                              // ...then release them together
        for (Thread t : threads) t.join();
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
