import java.util.*;

public class Practice {
    enum Decision { WAITING, COMMIT, ABORT }

    // Phase 1 of two-phase commit, as a pure state machine.
    static class Coordinator {
        private final Set<String> participants;
        private Decision decision = Decision.WAITING;

        Coordinator(Set<String> participants) { this.participants = participants; }

        Decision decision() { return decision; }

        // A participant's vote arrives. Returns the decision afterwards.
        Decision onVote(String from, boolean yes) {
            throw new UnsupportedOperationException("TODO: no -> ABORT; the last missing yes -> COMMIT; once decided, never change");
        }

        // The votes took too long. Returns the decision afterwards.
        Decision onTimeout() {
            throw new UnsupportedOperationException("TODO: a coordinator still waiting gives up");
        }
    }

    // CHECKS
    static Coordinator coordinator() { return new Coordinator(Set.of("orders-db", "stock-db")); }

    public static void main(String[] args) {
        check("every yes commits", List.of(Decision.WAITING, Decision.COMMIT), () -> {
            Coordinator c = coordinator();
            return List.of(c.onVote("orders-db", true), c.onVote("stock-db", true));
        });
        check("one no aborts at once", Decision.ABORT, () -> coordinator().onVote("stock-db", false));
        check("a timeout with a vote missing aborts", Decision.ABORT, () -> {
            Coordinator c = coordinator();
            c.onVote("orders-db", true);
            return c.onTimeout();
        });
        check("a duplicated yes counts once", Decision.WAITING, () -> {
            Coordinator c = coordinator();
            c.onVote("orders-db", true);
            return c.onVote("orders-db", true);
        });
        check("a commit is final", List.of(Decision.COMMIT, Decision.COMMIT), () -> {
            Coordinator c = coordinator();
            c.onVote("orders-db", true);
            c.onVote("stock-db", true);
            return List.of(c.onTimeout(), c.decision());
        });
        check("an abort is final", Decision.ABORT, () -> {
            Coordinator c = new Coordinator(Set.of("a", "b", "c"));
            c.onVote("a", true);
            c.onTimeout();
            c.onVote("b", true);
            return c.onVote("c", true);
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
