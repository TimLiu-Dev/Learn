import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Raft leader election (§5.1–5.2 of the paper), simulated in one JVM.
// Time is virtual: nothing sleeps, and one seed always replays the same history.
public class Election {
    enum Role { FOLLOWER, CANDIDATE, LEADER }

    sealed interface Msg permits RequestVote, VoteReply, Heartbeat, HeartbeatReply { long term(); int from(); }
    record RequestVote(long term, int from) implements Msg {}
    record VoteReply(long term, int from, boolean granted) implements Msg {}
    record Heartbeat(long term, int from) implements Msg {}             // AppendEntries, no entries
    record HeartbeatReply(long term, int from) implements Msg {}

    static String name(int id) { return "n" + (id + 1); }

    static class Sim {
        final int n, timeoutMin, timeoutMax, heartbeatMs;    // election timeout range and heartbeat, ms
        final boolean buggyVoter, tracing;                   // buggyVoter forgets whom it voted for
        final Random rnd;
        long now = 0;
        private long seq = 0;
        private record Ev(long at, long seq, Runnable action) {}
        private final PriorityQueue<Ev> queue =
            new PriorityQueue<>(Comparator.comparingLong(Ev::at).thenComparingLong(Ev::seq));

        class Node {
            final int id;
            Role role = Role.FOLLOWER;
            long term = 0;                   // currentTerm
            Integer votedFor = null;         // votedFor, for the current term only
            final Set<Integer> votes = new HashSet<>();
            boolean up = true;
            int timerEpoch = 0;              // bumping it cancels the pending election timer
            Node(int id) { this.id = id; }
            String name() { return Election.name(id); }
        }

        final List<Node> nodes = new ArrayList<>();
        private final Set<List<Integer>> cut = new HashSet<>();    // links that drop every message
        final TreeMap<Long, Set<Integer>> leadersByTerm = new TreeMap<>();   // for the safety check
        final List<String> trace = new ArrayList<>();

        Sim(long seed, int n, int timeoutMin, int timeoutMax, int heartbeatMs, boolean buggyVoter, boolean tracing) {
            this.rnd = new Random(seed);
            this.n = n; this.timeoutMin = timeoutMin; this.timeoutMax = timeoutMax;
            this.heartbeatMs = heartbeatMs; this.buggyVoter = buggyVoter; this.tracing = tracing;
            for (int i = 0; i < n; i++) nodes.add(new Node(i));
        }
        Sim(long seed) { this(seed, 5, 150, 300, 50, false, true); }

        private void log(String text) { if (tracing) trace.add(String.format("%6d ms  %s", now, text)); }
        private void schedule(long delayMs, Runnable action) { queue.add(new Ev(now + delayMs, seq++, action)); }
        private boolean linkUp(int a, int b) { return !cut.contains(List.of(a, b)); }

        private void send(int from, int to, Msg msg) {
            if (!linkUp(from, to)) return;
            schedule(2L + rnd.nextInt(9), () -> {             // 2–10 ms on the wire
                if (linkUp(from, to) && nodes.get(to).up) deliver(nodes.get(to), msg);
            });
        }

        private void resetElectionTimer(Node node) {
            int epoch = ++node.timerEpoch;
            int timeout = timeoutMin + rnd.nextInt(timeoutMax - timeoutMin + 1);
            schedule(timeout, () -> {
                if (node.up && node.timerEpoch == epoch && node.role != Role.LEADER) startElection(node);
            });
        }

        private void startElection(Node node) {
            node.role = Role.CANDIDATE;
            node.term++;
            node.votedFor = node.id;
            node.votes.clear(); node.votes.add(node.id);
            log(node.name() + " times out: candidate for term " + node.term);
            resetElectionTimer(node);                         // a split vote retries after a new timeout
            for (Node peer : nodes) if (peer.id != node.id) send(node.id, peer.id, new RequestVote(node.term, node.id));
        }

        private void deliver(Node node, Msg msg) {
            if (msg.term() > node.term) {                     // any newer term: adopt it, become a follower
                if (node.role != Role.FOLLOWER) log(node.name() + " steps down: saw term " + msg.term());
                node.term = msg.term(); node.votedFor = null; node.role = Role.FOLLOWER;
            }
            switch (msg) {
                case RequestVote rv -> {
                    boolean free = node.votedFor == null || node.votedFor == rv.from() || buggyVoter;
                    boolean grant = rv.term() == node.term && free;
                    if (grant) {
                        node.votedFor = rv.from();
                        resetElectionTimer(node);
                        log(node.name() + " votes for " + name(rv.from()) + " in term " + rv.term());
                    }
                    send(node.id, rv.from(), new VoteReply(node.term, node.id, grant));
                }
                case VoteReply vr -> {
                    if (node.role == Role.CANDIDATE && vr.term() == node.term && vr.granted()) {
                        node.votes.add(vr.from());
                        if (node.votes.size() > n / 2) becomeLeader(node);
                    }
                }
                case Heartbeat hb -> {
                    if (hb.term() == node.term) {
                        if (node.role == Role.CANDIDATE) log(node.name() + " hears leader " + name(hb.from()) + ": back to follower");
                        node.role = Role.FOLLOWER;
                        resetElectionTimer(node);
                    }
                    send(node.id, hb.from(), new HeartbeatReply(node.term, node.id));   // a stale leader learns the new term
                }
                case HeartbeatReply hr -> {}                  // the term check above already did the work
            }
        }

        private void becomeLeader(Node node) {
            node.role = Role.LEADER;
            leadersByTerm.computeIfAbsent(node.term, t -> new TreeSet<>()).add(node.id);
            log(node.name() + " is LEADER of term " + node.term + ", votes from " +
                node.votes.stream().sorted().map(Election::name).collect(Collectors.joining(", ")));
            heartbeats(node, node.term);
        }

        private void heartbeats(Node node, long term) {
            if (!node.up || node.role != Role.LEADER || node.term != term) return;
            for (Node peer : nodes) if (peer.id != node.id) send(node.id, peer.id, new Heartbeat(term, node.id));
            schedule(heartbeatMs, () -> heartbeats(node, term));
        }

        void start() { for (Node node : nodes) resetElectionTimer(node); }
        void crash(int id) { nodes.get(id).up = false; log(name(id) + " CRASHES"); }
        void restart(int id) {                                // term and votedFor survive: Raft persists them
            Node node = nodes.get(id);
            node.up = true; node.role = Role.FOLLOWER;
            log(node.name() + " restarts as a follower, still in term " + node.term);
            resetElectionTimer(node);
        }
        void partition(Set<Integer> side) {
            for (int a = 0; a < n; a++) for (int b = 0; b < n; b++)
                if (side.contains(a) != side.contains(b)) cut.add(List.of(a, b));
            String left = side.stream().sorted().map(Election::name).collect(Collectors.joining(",", "{", "}"));
            String right = IntStream.range(0, n).filter(i -> !side.contains(i)).mapToObj(Election::name)
                .collect(Collectors.joining(",", "{", "}"));
            log("PARTITION " + left + " | " + right);
        }
        void heal() { cut.clear(); log("partition HEALS"); }
        void at(long timeMs, Runnable action) { schedule(timeMs - now, action); }
        void runUntil(long timeMs) {
            while (!queue.isEmpty() && queue.peek().at() <= timeMs) {
                Ev ev = queue.poll(); now = ev.at(); ev.action().run();
            }
            now = timeMs;
        }
        Node leader() {
            return nodes.stream().filter(x -> x.up && x.role == Role.LEADER).findFirst().orElse(null);
        }
    }

    // Random crashes, restarts, partitions and heals every 300 ms for 3 s; count terms by number of leaders.
    static int[] sweep(int seeds, boolean buggy) {
        int[] counts = new int[3];                            // terms with 0, 1, 2+ leaders
        boolean reported = false;
        for (int seed = 1; seed <= seeds; seed++) {
            Sim sim = new Sim(seed, 5, 150, 300, 50, buggy, false);
            sim.start();
            for (long t = 300; t <= 3_000; t += 300) {
                sim.runUntil(t);
                int r = sim.rnd.nextInt(4);
                int victim = sim.rnd.nextInt(sim.n);
                switch (r) {
                    case 0 -> { if (sim.nodes.stream().filter(x -> !x.up).count() < 2) sim.crash(victim); }
                    case 1 -> { if (!sim.nodes.get(victim).up) sim.restart(victim); }
                    case 2 -> {
                        Set<Integer> side = new HashSet<>();
                        for (int i = 0; i < sim.n; i++) if (sim.rnd.nextBoolean()) side.add(i);
                        sim.partition(side);
                    }
                    default -> sim.heal();
                }
            }
            sim.runUntil(3_500);
            long maxTerm = sim.nodes.stream().mapToLong(x -> x.term).max().orElse(0);
            for (long term = 1; term <= maxTerm; term++) {
                Set<Integer> ids = sim.leadersByTerm.get(term);
                int k = ids == null ? 0 : ids.size();
                counts[Math.min(k, 2)]++;
                if (k >= 2 && !reported) {
                    reported = true;
                    System.out.println("  first violation: seed " + seed + ", term " + term + ", leaders " +
                        ids.stream().map(Election::name).collect(Collectors.joining(" and ")));
                }
            }
        }
        return counts;
    }

    public static void main(String[] args) {
        System.out.println("== Seed 42: start, crash the leader at 600 ms, restart it at 1,200 ms");
        Sim sim = new Sim(42);
        sim.start();
        sim.runUntil(600);
        Sim.Node first = sim.leader();
        sim.crash(first.id);
        sim.at(1_200, () -> sim.restart(first.id));
        sim.runUntil(1_500);
        sim.trace.forEach(System.out::println);
        System.out.println("leaders by term: " + sim.leadersByTerm.entrySet().stream()
            .map(e -> "term " + e.getKey() + " -> " + e.getValue().stream().map(Election::name).collect(Collectors.joining(", ")))
            .collect(Collectors.joining(", ")));
        System.out.println("final terms: " + sim.nodes.stream().map(x -> x.name() + "=" + x.term).collect(Collectors.joining(" ")));

        System.out.println();
        System.out.println("== Safety sweep: 2,000 seeds, random crashes and partitions for 3 s each");
        int[] ok = sweep(2_000, false);
        System.out.println("  terms with no leader: " + ok[0] + ", one leader: " + ok[1] + ", two or more leaders: " + ok[2]);

        System.out.println();
        System.out.println("== The same sweep with a buggy voter that forgets whom it voted for");
        int[] bad = sweep(2_000, true);
        System.out.println("  terms with no leader: " + bad[0] + ", one leader: " + bad[1] + ", two or more leaders: " + bad[2]);
    }
}
