import java.util.List;
import java.util.Objects;

public class Practice {
    // What a voter remembers. votedFor is null when it has not voted in currentTerm;
    // lastLogIndex and lastLogTerm are 0 for an empty log.
    record Voter(long currentTerm, Integer votedFor, int lastLogIndex, long lastLogTerm) { }
    record RequestVote(long term, int candidateId, int lastLogIndex, long lastLogTerm) { }
    record Reply(Voter voter, boolean granted) { }

    // Is the candidate's log at least as up-to-date as the voter's?
    static boolean candidateIsUpToDate(Voter v, RequestVote req) {
        throw new UnsupportedOperationException("TODO: compare last log terms first; only if they're equal, compare last indexes");
    }

    static Reply onRequestVote(Voter v, RequestVote req) {
        throw new UnsupportedOperationException("TODO: stale term? newer term? already voted this term? log up to date?");
    }

    // CHECKS
    public static void main(String[] args) {
        check("a fresh voter grants and remembers its vote", new Reply(new Voter(3, 1, 5, 2), true),
            () -> onRequestVote(new Voter(3, null, 5, 2), new RequestVote(3, 1, 5, 2)));
        check("a request from an older term is refused and changes nothing", new Reply(new Voter(5, null, 0, 0), false),
            () -> onRequestVote(new Voter(5, null, 0, 0), new RequestVote(4, 1, 9, 4)));
        check("one vote per term", new Reply(new Voter(3, 2, 0, 0), false),
            () -> onRequestVote(new Voter(3, 2, 0, 0), new RequestVote(3, 1, 0, 0)));
        check("the same candidate may ask again", new Reply(new Voter(3, 2, 0, 0), true),
            () -> onRequestVote(new Voter(3, 2, 0, 0), new RequestVote(3, 2, 0, 0)));
        check("a newer term frees the vote", new Reply(new Voter(4, 1, 4, 3), true),
            () -> onRequestVote(new Voter(3, 2, 4, 3), new RequestVote(4, 1, 4, 3)));
        check("the last term decides before the length", List.of(true, false, false, true), () -> {
            Voter v = new Voter(5, null, 10, 2);
            return List.of(onRequestVote(v, new RequestVote(5, 1, 3, 3)).granted(),
                           onRequestVote(v, new RequestVote(5, 1, 20, 1)).granted(),
                           onRequestVote(v, new RequestVote(5, 1, 9, 2)).granted(),
                           onRequestVote(v, new RequestVote(5, 1, 10, 2)).granted());
        });
        check("a newer term is adopted even when the vote is refused", new Reply(new Voter(5, null, 10, 3), false),
            () -> onRequestVote(new Voter(3, 2, 10, 3), new RequestVote(5, 1, 4, 2)));
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
