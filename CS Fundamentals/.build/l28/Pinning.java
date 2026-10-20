import java.util.ArrayList;
import java.util.List;

// run with: java -Djdk.virtualThreadScheduler.parallelism=1 Pinning.java   (one carrier thread)
public class Pinning {
    static class SlowInit {                       // its static initializer blocks for 300 ms
        static { sleep(300); }
        static void touch() {}
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }

    static void time(String label, List<Runnable> jobs) throws InterruptedException {
        long start = System.nanoTime();
        List<Thread> threads = new ArrayList<>();
        for (Runnable job : jobs) threads.add(Thread.startVirtualThread(job));
        for (Thread t : threads) t.join();
        System.out.printf("%-40s %4d ms%n", label, (System.nanoTime() - start) / 1_000_000);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("java " + Runtime.version().feature());
        List<Runnable> jobs = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Object lock = new Object();
            jobs.add(() -> { synchronized (lock) { sleep(300); } });   // block while holding a monitor
        }
        time("4 x sleep(300) inside synchronized", jobs);

        jobs = new ArrayList<>();
        jobs.add(SlowInit::touch);                                     // blocks inside <clinit>
        for (int i = 0; i < 3; i++) jobs.add(() -> sleep(300));
        time("1 class init + 3 plain sleeps (300 each)", jobs);
    }
}
