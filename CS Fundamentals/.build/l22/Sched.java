import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

public class Sched {
    public static void main(String[] args) throws InterruptedException {
        // 1. More busy threads than cores. None of them ever sleeps or yields.
        int cores = Runtime.getRuntime().availableProcessors();
        int n = cores * 2;
        int seconds = 3;
        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        long[] cpuMs = new long[n];
        long deadline = System.nanoTime() + seconds * 1_000_000_000L;
        List<Thread> spinners = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = i;
            spinners.add(new Thread(() -> {
                long x = 0;
                while (System.nanoTime() < deadline) x++;               // pure spinning
                cpuMs[id] = mx.getCurrentThreadCpuTime() / 1_000_000;   // CPU time this thread actually got
                if (x == 42) System.out.println("unlikely");            // keep the loop from being removed
            }));
        }
        for (Thread t : spinners) t.start();
        for (Thread t : spinners) t.join();
        System.out.println(n + " spinning threads on " + cores + " cores for " + seconds + " s of wall time");
        System.out.printf("CPU time per thread: min %d ms, max %d ms, average %d ms%n",
                Arrays.stream(cpuMs).min().getAsLong(), Arrays.stream(cpuMs).max().getAsLong(),
                (long) Arrays.stream(cpuMs).average().getAsDouble());

        // 2. Pass a token back and forth: every handoff blocks one thread and wakes the other.
        int rounds = 100_000;
        SynchronousQueue<Integer> ping = new SynchronousQueue<>();
        SynchronousQueue<Integer> pong = new SynchronousQueue<>();
        Thread partner = new Thread(() -> {
            try {
                for (int i = 0; i < rounds; i++) pong.put(ping.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        partner.start();
        long t = System.nanoTime();
        for (int i = 0; i < rounds; i++) { ping.put(i); pong.take(); }  // one round trip = two handoffs
        double ns = (double) (System.nanoTime() - t) / (2 * rounds);
        partner.join();
        System.out.printf("one thread-to-thread handoff: %.1f µs%n", ns / 1000);
    }
}
