import java.util.ArrayList;
import java.util.List;

public class Ex1 {
    public static void main(String[] args) throws InterruptedException {
        int[] data = new int[40_000_000];
        for (int i = 0; i < data.length; i++) data[i] = i % 7;
        int parts = 4;
        long[] partial = new long[parts];               // one slot per thread: nothing shared
        int chunk = data.length / parts;

        long start = System.nanoTime();
        List<Thread> workers = new ArrayList<>();
        for (int p = 0; p < parts; p++) {
            int from = p * chunk, to = (p + 1) * chunk, slot = p;
            Thread t = new Thread(() -> {
                long sum = 0;                           // a local: lives on this thread's own stack
                for (int i = from; i < to; i++) sum += data[i];
                partial[slot] = sum;                    // each thread writes only its own slot
            }, "summer-" + p);
            t.start();
            workers.add(t);
        }
        for (Thread t : workers) t.join();              // join also makes the slots visible to main
        long total = 0;
        for (long s : partial) total += s;
        long ms = (System.nanoTime() - start) / 1_000_000;

        long s2 = System.nanoTime();
        long check = 0;
        for (int x : data) check += x;
        long ms2 = (System.nanoTime() - s2) / 1_000_000;

        System.out.println("4 threads: " + total + " in " + ms + " ms; 1 thread: " + check + " in " + ms2 + " ms");
        StringBuilder sb = new StringBuilder();
        for (Thread t : workers) sb.append(sb.isEmpty() ? "" : ", ").append(t.getName()).append("=").append(t.getState());
        System.out.println(sb);
    }
}
