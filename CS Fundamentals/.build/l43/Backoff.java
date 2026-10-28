import java.util.*;

// A simulation, like the one in AWS's "Exponential Backoff and Jitter" post:
// 100 clients all fail at t=0. The server is down until t=1000 ms, then it can
// serve 10 requests per 100 ms window; anything beyond that fails too.
// Every failed client sleeps, then tries again.
public class Backoff {
    static final int CLIENTS = 100;
    static final long BASE = 50;      // first backoff, ms
    static final long CAP = 3200;     // backoff never grows beyond this

    static int[] simulate(boolean jitter) {
        Random random = new Random(43);
        int[] attemptsPerWindow = new int[600];                 // 600 windows of 100 ms = 60 s
        int[] servedPerWindow = new int[600];
        PriorityQueue<long[]> queue = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));  // [time, client, attempt]
        for (int c = 0; c < CLIENTS; c++) queue.add(new long[] { 0, c, 0 });
        int totalAttempts = 0;
        long lastSuccess = 0;
        while (!queue.isEmpty()) {
            long[] e = queue.poll();
            long t = e[0], c = e[1], attempt = e[2];
            int w = (int) (t / 100);
            totalAttempts++;
            attemptsPerWindow[w]++;
            boolean ok = t >= 1000 && servedPerWindow[w] < 10;
            if (ok) { servedPerWindow[w]++; lastSuccess = Math.max(lastSuccess, t); continue; }
            long backoff = Math.min(CAP, BASE << Math.min(attempt, 20));
            long sleep = jitter ? (long) (random.nextDouble() * backoff) : backoff;   // full jitter: 0..backoff
            queue.add(new long[] { t + sleep, c, attempt + 1 });
        }
        System.out.println("  total attempts: " + totalAttempts + ", last client served at " + lastSuccess + " ms");
        return attemptsPerWindow;
    }

    static void show(int[] attempts) {
        for (int w = 0; w < 40; w += 2) {
            int n = attempts[w] + attempts[w + 1];
            System.out.printf("  %4d ms %4d %s%n", w * 100, n, "#".repeat((n + 7) / 8));
        }
    }

    public static void main(String[] args) {
        System.out.println("== exponential backoff, no jitter (attempts per 200 ms)");
        show(simulate(false));
        System.out.println("== exponential backoff with full jitter");
        show(simulate(true));
    }
}
