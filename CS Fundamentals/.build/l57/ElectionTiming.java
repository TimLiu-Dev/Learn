import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Exercise: how much randomness does the election timeout need? (Election.java must sit beside it)
// Like the Raft paper's Figure 16: crash the leader at a random point in its heartbeat
// interval, then measure how long the cluster has no leader.
public class ElectionTiming {
    static long downtime(long seed, int min, int max) {
        Election.Sim sim = new Election.Sim(seed, 5, min, max, min / 2, false, false);
        sim.start();
        while (sim.leader() == null && sim.now < 10_000) sim.runUntil(sim.now + 1);
        if (sim.leader() == null) return 10_000;             // never elected a first leader in 10 s
        sim.runUntil(sim.now + 500);                         // let it settle
        sim.runUntil(sim.now + sim.rnd.nextInt(min / 2));
        Election.Sim.Node old = sim.leader();                // a healthy cluster keeps its leader
        sim.crash(old.id);
        long crashedAt = sim.now;
        while (sim.now < crashedAt + 10_000) {
            sim.runUntil(sim.now + 1);
            if (sim.leader() != null) return sim.now - crashedAt;
        }
        return 10_000;                                      // gave up: still no leader after 10 s
    }

    public static void main(String[] args) {
        System.out.println(String.format("%-14s %8s %8s %10s", "timeout", "median", "worst", "over 10 s"));
        int[][] ranges = {{150, 150}, {150, 151}, {150, 155}, {150, 175}, {150, 200}, {150, 300}};
        for (int[] r : ranges) {
            List<Long> times = new ArrayList<>();
            for (long seed = 1; seed <= 1_000; seed++) {
                times.add(downtime(seed, r[0], r[1]));
            }
            Collections.sort(times);
            long over = times.stream().filter(t -> t >= 10_000).count();
            System.out.println(String.format("%-14s %6d ms %6d ms %10d", r[0] + "-" + r[1] + " ms",
                times.get(times.size() / 2), times.get(times.size() - 1), over));
        }
    }
}
