// 5 philosophers, 5 forks. Each needs the fork on the left and the right to eat.
import java.lang.management.ManagementFactory;

public class Philosophers {
    static final int N = 5, MEALS = 100_000;
    static final Object[] forks = new Object[N];
    static final int[] meals = new int[N];

    public static void main(String[] args) throws InterruptedException {
        boolean ordered = args.length > 0 && args[0].equals("ordered");
        for (int i = 0; i < N; i++) forks[i] = new Object();
        Thread[] diners = new Thread[N];
        for (int p = 0; p < N; p++) {
            final int me = p;
            int left = me, right = (me + 1) % N;
            int first = ordered ? Math.min(left, right) : left;     // the fix: lower number first
            int second = ordered ? Math.max(left, right) : right;
            diners[p] = new Thread(() -> {
                for (int m = 0; m < MEALS; m++) {
                    synchronized (forks[first]) {
                        synchronized (forks[second]) {
                            meals[me]++;
                        }
                    }
                }
            }, "philosopher-" + me);
            diners[p].setDaemon(true);
            diners[p].start();
        }
        for (Thread d : diners) d.join(2000);
        long[] stuck = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
        if (stuck != null) {
            System.out.println("DEADLOCK: " + stuck.length + " philosophers each hold one fork");
        } else {
            System.out.println("everyone ate " + MEALS + " meals");
        }
    }
}
