package raft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Raft (election + log replication, §5 of the paper) in a deterministic simulation.
// Left out: persistence to disk, snapshots and membership changes.
public class Sim {
    public enum Role { FOLLOWER, CANDIDATE, LEADER }

    public record Entry(long term, String command) {}

    sealed interface Msg permits RequestVote, VoteReply, AppendEntries, AppendReply { long term(); int from(); }
    record RequestVote(long term, int from, int lastIndex, long lastTerm) implements Msg {}
    record VoteReply(long term, int from, boolean granted) implements Msg {}
    record AppendEntries(long term, int from, int prevIndex, long prevTerm, List<Entry> entries, int leaderCommit) implements Msg {}
    record AppendReply(long term, int from, boolean success, int matchIndex) implements Msg {}

    /** One line of history, plus who sent the message that caused it (for the widget). */
    public record Event(long at, String text, Integer from, Integer to) {}

    public final int n, heartbeatMs;
    public final Random rnd;
    private long now = 0;
    private long seq = 0;
    private record Ev(long at, long seq, Runnable action) {}
    private final PriorityQueue<Ev> queue =
        new PriorityQueue<>(Comparator.comparingLong(Ev::at).thenComparingLong(Ev::seq));

    public class Node {
        public final int id;
        public final String name;
        public Role role = Role.FOLLOWER;
        public long term = 0;                        // persistent: currentTerm
        public Integer votedFor = null;              // persistent: votedFor
        public final List<Entry> log = new ArrayList<>();      // persistent: log.get(i - 1) holds index i
        public int commitIndex = 0;                  // volatile
        public final List<String> applied = new ArrayList<>(); // the state machine: commands applied, in order
        final Set<Integer> votes = new HashSet<>();
        final int[] nextIndex = new int[n];          // leader only
        final int[] matchIndex = new int[n];         // leader only
        public boolean up = true;
        int timerEpoch = 0;

        Node(int id) { this.id = id; this.name = "n" + (id + 1); }
        int lastIndex() { return log.size(); }
        long lastTerm() { return log.isEmpty() ? 0 : log.get(log.size() - 1).term(); }
        long termAt(int index) { return index == 0 ? 0 : log.get(index - 1).term(); }
        /** The election restriction (§5.4.1): is a candidate's log at least as up-to-date as mine? */
        public boolean isUpToDate(int lastIndex, long lastTerm) {
            return lastTerm > lastTerm() || (lastTerm == lastTerm() && lastIndex >= lastIndex());
        }
    }

    public final List<Node> nodes = new ArrayList<>();
    private final Set<List<Integer>> cut = new HashSet<>();
    private Set<Integer> side = null;                // one side of the current partition, if any
    final TreeMap<Long, Set<Integer>> leadersByTerm = new TreeMap<>();
    public final List<Event> history = new ArrayList<>();
    public Consumer<Event> onEvent = e -> {};

    public Sim(long seed, int n, int heartbeatMs) {
        this.rnd = new Random(seed); this.n = n; this.heartbeatMs = heartbeatMs;
        for (int i = 0; i < n; i++) nodes.add(new Node(i));
    }
    public Sim(long seed) { this(seed, 5, 50); }

    public long now() { return now; }
    public Set<Integer> side() { return side; }

    private void log(String text, Integer from, Integer to) {
        Event e = new Event(now, text, from, to);
        history.add(e);
        onEvent.accept(e);
    }
    private void log(String text) { log(text, null, null); }
    private void schedule(long delayMs, Runnable action) { queue.add(new Ev(now + delayMs, seq++, action)); }
    private boolean linkUp(int a, int b) { return !cut.contains(List.of(a, b)); }

    private void send(int from, int to, Msg msg) {
        if (!linkUp(from, to)) return;
        schedule(2L + rnd.nextInt(9), () -> { if (linkUp(from, to) && nodes.get(to).up) deliver(nodes.get(to), msg); });
    }

    private void resetElectionTimer(Node node) {
        int epoch = ++node.timerEpoch;
        schedule(150L + rnd.nextInt(151), () -> {
            if (node.up && node.timerEpoch == epoch && node.role != Role.LEADER) startElection(node);
        });
    }

    private void startElection(Node node) {
        node.role = Role.CANDIDATE;
        node.term++;
        node.votedFor = node.id;
        node.votes.clear(); node.votes.add(node.id);
        log(node.name + " times out: candidate for term " + node.term);
        resetElectionTimer(node);
        RequestVote rv = new RequestVote(node.term, node.id, node.lastIndex(), node.lastTerm());
        for (Node peer : nodes) if (peer.id != node.id) send(node.id, peer.id, rv);
    }

    private void deliver(Node node, Msg msg) {
        if (msg.term() > node.term) {
            if (node.role != Role.FOLLOWER) log(node.name + " steps down: saw term " + msg.term(), msg.from(), node.id);
            node.term = msg.term(); node.votedFor = null; node.role = Role.FOLLOWER;
        }
        switch (msg) {
            case RequestVote rv -> {
                boolean grant = rv.term() == node.term &&
                    (node.votedFor == null || node.votedFor == rv.from()) &&
                    node.isUpToDate(rv.lastIndex(), rv.lastTerm());
                if (grant) {
                    node.votedFor = rv.from();
                    resetElectionTimer(node);
                    log(node.name + " votes for n" + (rv.from() + 1) + " in term " + rv.term(), node.id, rv.from());
                }
                send(node.id, rv.from(), new VoteReply(node.term, node.id, grant));
            }
            case VoteReply vr -> {
                if (node.role == Role.CANDIDATE && vr.term() == node.term && vr.granted()) {
                    node.votes.add(vr.from());
                    if (node.votes.size() > n / 2) becomeLeader(node);
                }
            }
            case AppendEntries ae -> onAppendEntries(node, ae);
            case AppendReply ar -> { if (node.role == Role.LEADER && ar.term() == node.term) onAppendReply(node, ar); }
        }
    }

    private void onAppendEntries(Node node, AppendEntries msg) {
        if (msg.term() < node.term) { send(node.id, msg.from(), new AppendReply(node.term, node.id, false, 0)); return; }
        if (node.role == Role.CANDIDATE) log(node.name + " hears leader n" + (msg.from() + 1) + ": back to follower", msg.from(), node.id);
        node.role = Role.FOLLOWER;
        resetElectionTimer(node);
        // Consistency check: my log must hold the entry just before the new ones.
        if (msg.prevIndex() > node.lastIndex() || node.termAt(msg.prevIndex()) != msg.prevTerm()) {
            send(node.id, msg.from(), new AppendReply(node.term, node.id, false, 0)); return;
        }
        int index = msg.prevIndex();
        for (Entry entry : msg.entries()) {
            index++;
            if (index <= node.lastIndex() && node.termAt(index) != entry.term()) {
                String dropped = node.log.subList(index - 1, node.log.size()).stream()
                    .map(Entry::command).collect(Collectors.joining(", "));
                while (node.log.size() >= index) node.log.remove(node.log.size() - 1);
                log(node.name + " drops uncommitted [" + dropped + "]: conflicts with the leader", msg.from(), node.id);
            }
            if (index > node.lastIndex()) node.log.add(entry);
        }
        if (msg.leaderCommit() > node.commitIndex) {
            node.commitIndex = Math.min(msg.leaderCommit(), msg.prevIndex() + msg.entries().size());
            apply(node);
        }
        send(node.id, msg.from(), new AppendReply(node.term, node.id, true, msg.prevIndex() + msg.entries().size()));
    }

    private void onAppendReply(Node leader, AppendReply msg) {
        if (!msg.success()) { leader.nextIndex[msg.from()] = Math.max(1, leader.nextIndex[msg.from()] - 1); return; }
        leader.matchIndex[msg.from()] = Math.max(leader.matchIndex[msg.from()], msg.matchIndex());
        leader.nextIndex[msg.from()] = leader.matchIndex[msg.from()] + 1;
        // Commit the highest index stored on a majority, but only by counting entries of the current term (§5.4.2).
        for (int index = leader.lastIndex(); index > leader.commitIndex; index--) {
            final int i = index;
            long copies = Arrays.stream(leader.matchIndex).filter(m -> m >= i).count();
            if (leader.termAt(index) == leader.term && copies > n / 2) {
                leader.commitIndex = index;
                log(leader.name + " commits up to index " + index + " (" + leader.log.get(index - 1).command() + "), stored on " + copies + " of " + n);
                apply(leader);
                break;
            }
        }
    }

    private void apply(Node node) {
        while (node.applied.size() < node.commitIndex) node.applied.add(node.log.get(node.applied.size()).command());
    }

    private void becomeLeader(Node node) {
        node.role = Role.LEADER;
        leadersByTerm.computeIfAbsent(node.term, t -> new TreeSet<>()).add(node.id);
        Arrays.fill(node.nextIndex, node.lastIndex() + 1);
        Arrays.fill(node.matchIndex, 0);
        node.matchIndex[node.id] = node.lastIndex();
        log(node.name + " is LEADER of term " + node.term + ", votes from " +
            node.votes.stream().sorted().map(i -> "n" + (i + 1)).collect(Collectors.joining(", ")));
        heartbeats(node, node.term);
    }

    // Each heartbeat is an AppendEntries carrying whatever that follower is missing.
    private void heartbeats(Node node, long term) {
        if (!node.up || node.role != Role.LEADER || node.term != term) return;
        for (Node peer : nodes) if (peer.id != node.id) {
            int prev = node.nextIndex[peer.id] - 1;
            List<Entry> entries = List.copyOf(node.log.subList(prev, node.log.size()));
            send(node.id, peer.id, new AppendEntries(term, node.id, prev, node.termAt(prev), entries, node.commitIndex));
        }
        schedule(heartbeatMs, () -> heartbeats(node, term));
    }

    /** A client asks node id to run a command. Only a leader accepts; acceptance is not commitment. */
    public boolean propose(int id, String command) {
        Node node = nodes.get(id);
        if (!node.up || node.role != Role.LEADER) return false;
        node.log.add(new Entry(node.term, command));
        node.matchIndex[node.id] = node.lastIndex();
        log("client -> " + node.name + ": " + command + " (index " + node.lastIndex() + ", term " + node.term + ", not committed yet)");
        return true;
    }

    public void start() { for (Node node : nodes) resetElectionTimer(node); }
    public void crash(int id) { nodes.get(id).up = false; log(nodes.get(id).name + " CRASHES"); }
    public void restart(int id) {                    // term, votedFor and log are persistent; the rest is rebuilt
        Node node = nodes.get(id);
        node.up = true; node.role = Role.FOLLOWER;
        node.commitIndex = 0; node.applied.clear();
        log(node.name + " restarts");
        resetElectionTimer(node);
    }
    public void partition(Set<Integer> side) {
        for (int a = 0; a < n; a++) for (int b = 0; b < n; b++)
            if (side.contains(a) != side.contains(b)) cut.add(List.of(a, b));
        this.side = side;
        Set<Integer> other = IntStream.range(0, n).filter(i -> !side.contains(i)).boxed().collect(Collectors.toSet());
        log("PARTITION " + group(side) + " | " + group(other));
    }
    public void heal() { cut.clear(); side = null; log("partition HEALS"); }
    public void at(long timeMs, Runnable action) { schedule(timeMs - now, action); }
    public void runUntil(long timeMs) {
        while (!queue.isEmpty() && queue.peek().at() <= timeMs) {
            Ev ev = queue.poll(); now = ev.at(); ev.action().run();
        }
        now = timeMs;
    }
    public Node leader() {
        return nodes.stream().filter(x -> x.up && x.role == Role.LEADER)
            .max(Comparator.comparingLong(x -> x.term)).orElse(null);
    }

    /** Election Safety: at most one leader per term. */
    public boolean electionSafe() { return leadersByTerm.values().stream().allMatch(s -> s.size() <= 1); }
    /** State Machine Safety: no two nodes applied different commands at the same index. */
    public boolean stateMachineSafe() {
        for (Node a : nodes) for (Node b : nodes)
            for (int i = 0; i < Math.min(a.applied.size(), b.applied.size()); i++)
                if (!a.applied.get(i).equals(b.applied.get(i))) return false;
        return true;
    }

    private static String group(Set<Integer> ids) {
        return ids.stream().sorted().map(i -> "n" + (i + 1)).collect(Collectors.joining(",", "{", "}"));
    }
}
