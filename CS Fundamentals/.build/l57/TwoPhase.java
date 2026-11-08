import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Two-phase commit: one coordinator, two participants, simulated in one JVM.
public class TwoPhase {
    enum TxState { WORKING, PREPARED, COMMITTED, ABORTED }

    static class Participant {
        final String name;
        private final boolean canCommit;
        private TxState state = TxState.WORKING;
        Participant(String name, boolean canCommit) { this.name = name; this.canCommit = canCommit; }
        TxState state() { return state; }
        boolean holdsLocks() { return state == TxState.WORKING || state == TxState.PREPARED; }

        boolean prepare() {                       // phase 1: promise to commit if asked, keep the locks
            state = canCommit ? TxState.PREPARED : TxState.ABORTED;
            return canCommit;
        }
        void finish(boolean commit) { state = commit ? TxState.COMMITTED : TxState.ABORTED; }
        // Once PREPARED, a participant may neither commit nor abort on its own.
        String read() { return holdsLocks() ? "BLOCKED: row locked by an in-doubt transaction" : "ok"; }
    }

    static class Coordinator {
        final List<String> log = new ArrayList<>();   // its durable decision log
        private final boolean crashBeforePhase2;
        Coordinator(boolean crashBeforePhase2) { this.crashBeforePhase2 = crashBeforePhase2; }
        Coordinator() { this(false); }

        void run(String tx, List<Participant> parts) {
            List<Boolean> votes = parts.stream().map(Participant::prepare).toList();   // real ones also time out silent participants
            boolean commit = votes.stream().allMatch(v -> v);
            log.add(tx + " " + (commit ? "COMMIT" : "ABORT"));
            String shown = IntStream.range(0, parts.size())
                .mapToObj(i -> parts.get(i).name + "=" + (votes.get(i) ? "yes" : "no")).collect(Collectors.joining(", "));
            System.out.println("  votes " + shown + " -> coordinator logs " + (commit ? "COMMIT" : "ABORT"));
            if (crashBeforePhase2) { System.out.println("  coordinator CRASHES before phase 2"); return; }
            parts.forEach(p -> p.finish(commit));
        }

        void recover(String tx, List<Participant> parts) {
            String decision = log.stream().filter(l -> l.startsWith(tx)).reduce((a, b) -> b).orElseThrow();
            boolean commit = decision.endsWith("COMMIT");
            System.out.println("  coordinator restarts, reads its log: " + (commit ? "COMMIT" : "ABORT") + ", tells everyone");
            parts.forEach(p -> p.finish(commit));
        }
    }

    static String show(List<Participant> parts) {
        return parts.stream().map(p -> p.name + "=" + p.state()).collect(Collectors.joining("  "));
    }

    public static void main(String[] args) {
        System.out.println("== 1. Both participants can commit");
        List<Participant> ok = List.of(new Participant("orders-db", true), new Participant("stock-db", true));
        new Coordinator().run("tx1", ok);
        System.out.println("  " + show(ok));

        System.out.println("== 2. stock-db can't (out of stock)");
        List<Participant> no = List.of(new Participant("orders-db", true), new Participant("stock-db", false));
        new Coordinator().run("tx2", no);
        System.out.println("  " + show(no));

        System.out.println("== 3. Both vote yes, then the coordinator crashes");
        List<Participant> stuck = List.of(new Participant("orders-db", true), new Participant("stock-db", true));
        Coordinator coord = new Coordinator(true);
        coord.run("tx3", stuck);
        System.out.println("  " + show(stuck));
        for (int s : new int[]{1, 60, 3_600}) System.out.println("  after " + s + "s, a reader on orders-db: " + stuck.get(0).read());
        System.out.println("  orders-db asks stock-db what to do: it is " + stuck.get(1).state() + " too, so neither knows the decision");
        coord.recover("tx3", stuck);
        System.out.println("  " + show(stuck) + "   reader: " + stuck.get(0).read());
    }
}
